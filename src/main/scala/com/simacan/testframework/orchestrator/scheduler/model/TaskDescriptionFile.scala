package com.simacan.testframework.orchestrator.scheduler.model

import play.api.libs.json.{Json, OFormat}

case class TaskDescriptionFile(
  tasks: Seq[TaskDescription],
  defaultJobTemplate: Option[String],
  defaultJobQueue: Option[String]) {

  def validateContent: TaskDescriptionFile = TaskDescriptionFile(tasks.filter(_.hasValidCron == false), None, None)
}

object TaskDescriptionFile {
  implicit val formatTaskDescriptionFile: OFormat[TaskDescriptionFile] =
    Json.format[TaskDescriptionFile]
}
