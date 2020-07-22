package com.simacan.testframework.orchestrator.scheduler.tasks

import com.simacan.testframework.orchestrator.config.{AppConfigurationStub, GatlingJobConfiguration}
import com.simacan.testframework.orchestrator.helpers._
import com.simacan.testframework.orchestrator.scheduler.model._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class AwsBatchTaskSpec extends AnyWordSpec with Matchers with Slf4jLogTester {

  val taskDescriptions: Seq[TaskDescription] =
    TaskDescriptionFileLoader.loadSchedule("jobSchedule.json").tasks

  val AwsConfiguration: GatlingJobConfiguration =
    AppConfigurationStub.gatlingJobConfiguration

  "An AwsBatchTask" should {

    "implement the Task interface / class" in {

      AwsBatchTask.create(AwsConfiguration) shouldBe a[Task[_]]
    }

    "contain empty containerOverrides after initialization" in {
      val awsBatchTaSK = AwsBatchTask.create(AwsConfiguration)
      awsBatchTaSK.containerOverrides.toString shouldBe "{}"
      awsBatchTaSK.hasEmptyContainerOverrides shouldBe true
    }

    "contain an empty SubmitJobRequest" in {
      val awsBatchTaSK = AwsBatchTask.create(AwsConfiguration)
      awsBatchTaSK.submitJobRequest.toString shouldBe "{}"
    }

    "Before adding the Jobname, the SubmitJobRequest should contain no jobname" in {
      val awsBatchTaSK = AwsBatchTask.create(AwsConfiguration)
      awsBatchTaSK.submitJobRequest.getJobName shouldBe null
    }

    "After adding the Jobname, the SubmitJobRequest should contain the given name" in {
      val awsBatchTaSK =
        AwsBatchTask.create(AwsConfiguration).withJobName("test")

      awsBatchTaSK.submitJobRequest.getJobName shouldBe "test"
    }

    "Adding the JobName should sanitize the taskName and update the taskDescription accordingly" in {
      val awsBatchTaSK = AwsBatchTask
        .create(AwsConfiguration)
        .withJobName("1.This name is sanitized!")

      awsBatchTaSK.submitJobRequest.getJobName shouldBe "T1This_name_is_sanitized"
      awsBatchTaSK.taskDescription.name shouldBe "T1This_name_is_sanitized"
    }

    "After adding the JobQueue, the SubmitJobRequest should contain the given jobQueue" in {
      val awsBatchTaSK =
        AwsBatchTask.create(AwsConfiguration).withJobQueue("test")

      awsBatchTaSK.submitJobRequest.getJobQueue shouldBe "test"
      awsBatchTaSK.taskDescription.gatlingJob.get.jobQueue.get shouldBe "test"
    }

    "After adding the JobDefinition, the SubmitJobRequest should contain the given jobDefinition" in {
      val awsBatchTaSK =
        AwsBatchTask.create(AwsConfiguration).withJobDefinition("test")

      awsBatchTaSK.submitJobRequest.getJobDefinition shouldBe "test"
      awsBatchTaSK.taskDescription.gatlingJob.get.jobTemplate.get shouldBe "test"
    }

    "After adding the containerOptions, the SubmitJobRequest should contain the given containerOptions" in {
      val awsBatchTask =
        AwsBatchTask.create(AwsConfiguration).withContainerOptions(Some(ContainerOptions(Some(5), Some(1024))))

      awsBatchTask.containerOverrides.getVcpus shouldBe 5
      awsBatchTask.containerOverrides.getMemory shouldBe 1024
    }

    "After adding empty containerOptions, the SubmitJobRequest should not contain values for vCpu and Memory" in {
      val awsBatchTask = AwsBatchTask.create(AwsConfiguration)
      awsBatchTask.containerOverrides.getVcpus shouldBe null
      awsBatchTask.containerOverrides.getMemory shouldBe null

      awsBatchTask.withContainerOptions(None)
      awsBatchTask.containerOverrides.getVcpus shouldBe null
      awsBatchTask.containerOverrides.getMemory shouldBe null

    }

    "commandString is setup correctly" in {
      val awsBatchTaSK =
        AwsBatchTask.create(AwsConfiguration).withScript("testScript")

      awsBatchTaSK.containerOverrides.getCommand should contain("-s testScript")
    }

    "commandString is setup correctly with description" in {
      val awsBatchTaSK =
        AwsBatchTask
          .create(AwsConfiguration)
          .withScript("testScript", "description")

      awsBatchTaSK.containerOverrides.getCommand should contain(
        "-s testScript -rd description"
      )
    }

    "jobOptions are added to environment settings of containerOverrides" in {
      // EncryptionKey used: ENCRYPTION_KEY=0102030405060708090a0b0c0d0e0f10
      val awsBatchTaSK = AwsBatchTask
        .create(AwsConfiguration)
        .withJobOptions(taskDescriptions.head.gatlingJob.get.options.get)

      awsBatchTaSK.containerOverrides.getEnvironment should have size 2
      awsBatchTaSK.containerOverrides.getEnvironment
        .get(0)
        .getValue shouldBe "gs_user_value0"
      awsBatchTaSK.containerOverrides.getEnvironment
        .get(1)
        .getValue shouldBe "gs_secret_value0"
    }

    "containerOverrides should be empty after adding an empty list of jobOptions" in {
      val awsBatchTaSK = AwsBatchTask
        .create(AwsConfiguration)
        .withJobOptions(Seq.empty[GatlingJobOption])

      awsBatchTaSK.hasEmptyContainerOverrides shouldBe true
    }

  }

  "After adding a TaskDescription to AwsBatchTask it" should {

    "TaskDescription(0)" in {
      val awsBatchTaSK = AwsBatchTask
        .create(AwsConfiguration)
        .withTaskDescription(taskDescriptions.head)

      awsBatchTaSK.submitJobRequest.getJobName shouldBe "task0"
      awsBatchTaSK.submitJobRequest.getJobDefinition shouldBe "Perf-JobDef"
      awsBatchTaSK.submitJobRequest.getJobQueue shouldBe "BatchJQ"
      awsBatchTaSK.containerOverrides.getCommand should contain(
        "-s script0 -rd description0"
      )
      awsBatchTaSK.containerOverrides.getEnvironment should have size 2

      awsBatchTaSK.cron shouldBe "*/1 * * * * ? *"
      awsBatchTaSK.name shouldBe "task0"

      awsBatchTaSK.taskDescription.name shouldBe "task0"
      awsBatchTaSK.taskDescription.shellJob shouldBe empty
      awsBatchTaSK.taskDescription.gatlingJob.get.jobTemplate.get shouldBe "Perf-JobDef"
      awsBatchTaSK.taskDescription.gatlingJob.get.jobQueue.get shouldBe "BatchJQ"
    }

    "TaskDescription(1)" in {
      val awsBatchTaSK = AwsBatchTask
        .create(AwsConfiguration)
        .withTaskDescription(taskDescriptions(1))

      awsBatchTaSK.submitJobRequest.getJobName shouldBe "task1"
      awsBatchTaSK.submitJobRequest.getJobDefinition shouldBe "PTC_MegaSim"
      awsBatchTaSK.submitJobRequest.getJobQueue shouldBe "PTC_TestQueue"
      awsBatchTaSK.containerOverrides.getCommand should contain("-s script1")
      awsBatchTaSK.containerOverrides.getCommand should not contain "-rd"
      awsBatchTaSK.containerOverrides.getEnvironment should have size 2
      awsBatchTaSK.containerOverrides.getVcpus shouldBe 5

      awsBatchTaSK.cron shouldBe "*/2 * * * * ? *"
      awsBatchTaSK.name shouldBe "task1"

      awsBatchTaSK.taskDescription.name shouldBe "task1"
      awsBatchTaSK.taskDescription.shellJob shouldBe empty
      awsBatchTaSK.taskDescription.gatlingJob.get.jobTemplate.get shouldBe "PTC_MegaSim"
      awsBatchTaSK.taskDescription.gatlingJob.get.jobQueue.get shouldBe "PTC_TestQueue"
      awsBatchTaSK.taskDescription.gatlingJob.get.containerOptions.get.vCpu.get shouldBe 5
    }

    "TaskDescription(2)" in {
      val awsBatchTaSK = AwsBatchTask
        .create(AwsConfiguration)
        .withTaskDescription(taskDescriptions(2))

      awsBatchTaSK.submitJobRequest.getJobName shouldBe "task2"
      awsBatchTaSK.submitJobRequest.getJobDefinition shouldBe "Perf-JobDef"
      awsBatchTaSK.submitJobRequest.getJobQueue shouldBe "BatchJQ"
      awsBatchTaSK.containerOverrides.getCommand should contain(
        "-s script2 -rd description2"
      )
      awsBatchTaSK.containerOverrides.getEnvironment shouldBe null
      awsBatchTaSK.cron shouldBe "*/3 * * * * ? *"
      awsBatchTaSK.name shouldBe "task2"

    }

    "JobSchedule(3) results in empty AwsBatchTask" in {
      val awsBatchTaSK = AwsBatchTask
        .create(AwsConfiguration)
        .withTaskDescription(taskDescriptions(3))

      awsBatchTaSK.submitJobRequest.toString shouldBe "{}"

      awsBatchTaSK.cron shouldBe "*/4 * * * * ? *"
      awsBatchTaSK.name shouldBe "task3"

      awsBatchTaSK.taskDescription.gatlingJob shouldBe empty

    }
  }

  "SanitizeJobName" should {
    "keep all allowed characters" in {
      AwsBatchTask.sanitizeJobName("Task: 2020-03-01 15:30:30") shouldBe "Task_2020-03-01_153030"
      AwsBatchTask.sanitizeJobName(
        "ABCDEFGHIJKLMNOPQRATUVWXYZabcdefghikjklmnopqrstuvwxyz!@#$%^&*() -_=+?><,.0123456789"
      ) shouldBe "ABCDEFGHIJKLMNOPQRATUVWXYZabcdefghikjklmnopqrstuvwxyz_-_0123456789"
    }

    "return a letter as the first character" in {
      AwsBatchTask.sanitizeJobName("ABC") shouldBe "ABC"
      AwsBatchTask.sanitizeJobName("abc") shouldBe "abc"
      AwsBatchTask.sanitizeJobName("123") shouldBe "T123"
      AwsBatchTask.sanitizeJobName(":123:") shouldBe "T123"
      AwsBatchTask.sanitizeJobName("_123:") shouldBe "T_123"
    }

    "return a string with maximum length of 128 characters" in {
      AwsBatchTask.sanitizeJobName("ABC") should have length 3
      AwsBatchTask.sanitizeJobName("123" * 50) should have length 128
    }
  }

  "Submit a job" should {

    "return a proper result for TaskDescription(0)" taggedAs (AwsConnectedTag, CodeShipSkip) in {
      val awsBatchTaSK = AwsBatchTask
        .create(AwsConfiguration)
        .withTaskDescription(taskDescriptions.head)

      awsBatchTaSK.submitJob().isSuccess shouldBe true
    }

    "return a faulty result for TaskDescription(1)" taggedAs (AwsConnectedTag, CodeShipSkip) in {
      val awsBatchTaSK = AwsBatchTask
        .create(AwsConfiguration)
        .withTaskDescription(taskDescriptions(1))

      val testLogger = TestLoggerFactory.getLogger(awsBatchTaSK.getClass)
      awsBatchTaSK.submitJob().isSuccess shouldBe false
      testLogger.logsList.size shouldBe 1
      testLogger.logsList.head.getMessage should include("AWS BatchJob submission failed: Job:")
    }

    "return a proper result for TaskDescription(0) by execute in Task" taggedAs (AwsConnectedTag, CodeShipSkip) in {
      val awsBatchTask = AwsBatchTask
        .create(AwsConfiguration)
        .withTaskDescription(taskDescriptions.head)

      val task: Task[_] = awsBatchTask
      task.execute() shouldBe true
    }

    "return a faulty result for TaskDescription(1) by execute in Task" taggedAs (AwsConnectedTag, CodeShipSkip) in {
      val awsBatchTask = AwsBatchTask
        .create(AwsConfiguration)
        .withTaskDescription(taskDescriptions(1))

      val task: Task[_] = awsBatchTask
      task.execute() shouldBe false
    }

  }

}
