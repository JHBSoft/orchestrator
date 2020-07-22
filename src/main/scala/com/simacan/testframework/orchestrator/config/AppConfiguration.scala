package com.simacan.testframework.orchestrator.config

import com.simacan.base.http.HttpConfiguration
import com.simacan.testframework.orchestrator.scheduler.model.TaskDescriptionFile

case class AppConfiguration(
  http: HttpConfiguration,
  httpGithub: HttpConfiguration,
  githubHandler: GithubHookHandlerServiceConfiguration,
  scheduler: SchedulerServiceConfiguration)

case class AuthConfiguration(
  username: String,
  password: String,
  realm: String,
)

case class GithubHookHandlerServiceConfiguration(
  githubhook: String,
  githubtoken: String,
  gitrepository: String,
  gitbranchref: String,
  localrepositorypath: String)

case class SchedulerServiceConfiguration(
  scheduleResource: String,
  encryptionKey: String,
  auth: AuthConfiguration,
  gatlingJob: GatlingJobConfiguration) {

  def withScheduleResource(scheduleFile: String): SchedulerServiceConfiguration =
    if (scheduleFile.isEmpty) this else copy(scheduleResource = scheduleFile)

  def merge(
    taskDescriptionFile: TaskDescriptionFile
  ): SchedulerServiceConfiguration =
    this.copy(gatlingJob = gatlingJob.merge(taskDescriptionFile))
}

case class GatlingJobConfiguration(
  defaultAwsJobDefinition: String,
  defaultAwsJobQueue: String,
  awsAccessKeyId: String,
  awsSecretAccessKey: String,
  awsRegion: String) {

  def merge(taskDescriptionFile: TaskDescriptionFile): GatlingJobConfiguration =
    this.copy(
      defaultAwsJobQueue = taskDescriptionFile.defaultJobQueue.getOrElse(this.defaultAwsJobQueue),
      defaultAwsJobDefinition = taskDescriptionFile.defaultJobTemplate
        .getOrElse(this.defaultAwsJobDefinition)
    )

}
