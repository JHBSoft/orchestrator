package com.simacan.testframework.orchestrator.scheduler.messages

import com.simacan.testframework.orchestrator.scheduler.model.TaskDescription
import play.api.libs.json.{Format, Json}

case class AllTasksResponse(tasks: Seq[TaskDescription])

object AllTasksResponse {
  implicit val allTasksResponseFormat: Format[AllTasksResponse] =
    Json.format[AllTasksResponse]
}
