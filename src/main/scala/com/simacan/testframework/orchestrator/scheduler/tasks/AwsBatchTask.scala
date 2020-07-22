package com.simacan.testframework.orchestrator.scheduler.tasks

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.batch.model.{ContainerOverrides, KeyValuePair, SubmitJobRequest, SubmitJobResult}
import com.amazonaws.services.batch.{AWSBatch, AWSBatchClientBuilder}
import com.simacan.testframework.orchestrator.config.GatlingJobConfiguration
import com.simacan.testframework.orchestrator.scheduler.model.{ContainerOptions, GatlingJobOption, TaskDescription}
import org.slf4j.LoggerFactory

import scala.math.min
import scala.util.{Failure, Try}

private[scheduler] case class AwsBatchTask(
  configuration: GatlingJobConfiguration,
  override val taskDescription: TaskDescription = TaskDescription.empty,
  submitJobRequest: SubmitJobRequest = new SubmitJobRequest(),
  containerOverrides: ContainerOverrides = new ContainerOverrides()
) extends Task[AwsBatchTask] {

  def withJobName(name: String): AwsBatchTask = {
    val sanitizedName = AwsBatchTask.sanitizeJobName(name)
    submitJobRequest.withJobName(sanitizedName)

    copy(taskDescription = taskDescription.copy(name = sanitizedName))
  }

  def withJobQueue(queueName: String): AwsBatchTask = {
    submitJobRequest.withJobQueue(queueName)

    val newGatlingJobDef =
      taskDescription.gatlingJob.get.copy(jobQueue = Some(queueName))

    copy(
      taskDescription = taskDescription.copy(gatlingJob = Some(newGatlingJobDef))
    )

  }

  def withJobDefinition(definitionName: String): AwsBatchTask = {
    submitJobRequest.withJobDefinition(definitionName)

    val newGatlingJobDef =
      taskDescription.gatlingJob.get.copy(jobTemplate = Some(definitionName))

    copy(
      taskDescription = taskDescription.copy(gatlingJob = Some(newGatlingJobDef))
    )

  }

  def withScript(scriptName: String, scriptDescription: String = ""): AwsBatchTask = {

    val descriptionPart: String =
      if (!scriptDescription.isEmpty) " -rd " + scriptDescription else ""
    val jobCommand = "-s " + scriptName + descriptionPart

    containerOverrides.withCommand(jobCommand)

    this
  }

  def withJobOptions(jobOptions: Seq[GatlingJobOption]): AwsBatchTask = {
    for (option <- jobOptions) {
      containerOverrides.withEnvironment(
        new KeyValuePair().withName(option.name).withValue(option.getValue)
      )
    }
    this
  }

  private[tasks] def withVCpu(vCpu: Option[Int]): AwsBatchTask = {
    if (vCpu.nonEmpty) containerOverrides.withVcpus(vCpu.get)
    this
  }

  private[tasks] def withMemory(memory: Option[Int]): AwsBatchTask = {
    if (memory.nonEmpty) containerOverrides.withMemory(memory.get)
    this
  }

  def withContainerOptions(containerOptions: Option[ContainerOptions]): AwsBatchTask = {
    if (containerOptions.nonEmpty) withVCpu(containerOptions.get.vCpu).withMemory(containerOptions.get.memory)
    this
  }

  override def withTaskDescription(
    taskDescription: TaskDescription
  ): AwsBatchTask = {

    if (taskDescription.gatlingJob.isDefined) {
      val gj = taskDescription.gatlingJob.get

      copy(taskDescription = taskDescription)
        .withJobName(taskDescription.name)
        .withJobDefinition(
          gj.jobTemplate.getOrElse(configuration.defaultAwsJobDefinition)
        )
        .withJobQueue(gj.jobQueue.getOrElse(configuration.defaultAwsJobQueue))
        .withJobOptions(gj.options.getOrElse(Seq.empty[GatlingJobOption]))
        .withScript(gj.script, gj.description.getOrElse(""))
        .withContainerOptions(gj.containerOptions)
    } else copy(taskDescription = taskDescription)

  }

  private[scheduler] def hasEmptyContainerOverrides: Boolean =
    containerOverrides.toString == "{}"

  private[scheduler] lazy val awsCreds: BasicAWSCredentials =
    new BasicAWSCredentials(
      configuration.awsAccessKeyId,
      configuration.awsSecretAccessKey
    )

  def submitJob(): Try[SubmitJobResult] = {
    val batchClient: AWSBatch =
      AWSBatchClientBuilder
        .standard()
        .withRegion(configuration.awsRegion)
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .build()

    // Clone the submitJobRequest in case the next time the containerOverrides have been changed.
    Try {
      batchClient.submitJob(
        submitJobRequest.clone().withContainerOverrides(containerOverrides)
      )
    } recoverWith {
      case e: Throwable =>
        LoggerFactory
          .getLogger(getClass)
          .warn(s"AWS BatchJob submission failed: Job: ${submitJobRequest.getJobName}", e)
        Failure(e)
    }
  }

  override def execute(): Boolean = {
    submitJob().isSuccess
  }
}

object AwsBatchTask {

  def create(configuration: GatlingJobConfiguration): AwsBatchTask = {
    new AwsBatchTask(configuration)
  }

  def sanitizeJobName(jobName: String): String = {
    var sanitizedJobName =
      jobName.replace(" ", "_").replaceAll("[^A-Za-z0-9_-]", "")
    if (!sanitizedJobName.charAt(0).isLetter)
      sanitizedJobName = "T" + sanitizedJobName

    sanitizedJobName.substring(0, min(sanitizedJobName.length, 128))
  }

}
