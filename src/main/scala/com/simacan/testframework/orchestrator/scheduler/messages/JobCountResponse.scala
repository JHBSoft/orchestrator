package com.simacan.testframework.orchestrator.scheduler.messages

import play.api.libs.json.{Format, Json}

case class JobCountResponse(jobCount: Int)

object JobCountResponse {
  implicit val formatJobCount: Format[JobCountResponse] =
    Json.format[JobCountResponse]
}
