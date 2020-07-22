package com.simacan.testframework.orchestrator.scheduler.messages

import play.api.libs.json.{Format, Json}

case class RestartRequest(scheduleFile: String)

object RestartRequest {
  implicit val formatRestartRequest: Format[RestartRequest] =
    Json.format[RestartRequest]
}
