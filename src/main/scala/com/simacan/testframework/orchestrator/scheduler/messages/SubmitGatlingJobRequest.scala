package com.simacan.testframework.orchestrator.scheduler.messages

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import com.simacan.testframework.orchestrator.scheduler.model.{GatlingJob, TaskDescription}
import play.api.libs.json.{Json, OFormat}

case class SubmitGatlingJobRequest(gatlingJob: GatlingJob, name: Option[String]) {

  def convertToTaskDescription(): TaskDescription =
    TaskDescription.empty.copy(
      name = name.getOrElse(SubmitGatlingJobRequest.getDefaultTaskName),
      gatlingJob = Some(gatlingJob)
    )
}

object SubmitGatlingJobRequest {
  implicit val formatSubmitAwsBatchTaSKRequest: OFormat[SubmitGatlingJobRequest] = Json.format[SubmitGatlingJobRequest]

  def getDefaultTaskName: String =
    "Task_" + OffsetDateTime
      .now()
      .format(DateTimeFormatter.ofPattern("YYYY-MM-dd_HHmmss"))
}
