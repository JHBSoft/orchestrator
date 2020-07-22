package com.simacan.testframework.orchestrator.scheduler.messages

import java.util.Date

import play.api.libs.json.{Json, OFormat}

case class TaskCronData(task: String, cronExpression: String, nextFiringTime: Date)

object TaskCronData {
  implicit val formatTaskCronData: OFormat[TaskCronData] =
    Json.format[TaskCronData]
}

case class GetAllCronDataResponse(tasks: Seq[TaskCronData])

object GetAllCronDataResponse {
  implicit val formatGetCronDataResponse: OFormat[GetAllCronDataResponse] =
    Json.format[GetAllCronDataResponse]
}
