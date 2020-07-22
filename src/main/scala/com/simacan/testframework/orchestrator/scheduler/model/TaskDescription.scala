package com.simacan.testframework.orchestrator.scheduler.model

import com.simacan.testframework.orchestrator.scheduler.tasks.TaskJob
import play.api.libs.json.{Format, Json}

import scala.util.{Failure, Success, Try}

/**
  * A description of a task to be executed by the orchestrator scheduler.
  * If both gatlingJob and shellJob are provided, the orchestrator treat it
  * as a gatlingJob. Any other job description is neglected.
  *
  * @param name  name of the task
  * @param cron  cronExpression
  * @param gatlingJob task definition for a gatling based job
  * @param shellJob task definition for a shell based job
  */
case class TaskDescription(name: String, cron: String, gatlingJob: Option[GatlingJob], shellJob: Option[String]) {

  val jobType: Int =
    if (gatlingJob.isDefined) JobType.Gatling
    else if (shellJob.isDefined) JobType.Shell
    else JobType.Other

  def isEmpty: Boolean = this.equals(TaskDescription.empty)

  def hasValidCron: Boolean =
    Try {
      TaskJob.getCronSchedule(cron)
    } match {
      case Success(_) => true
      case Failure(_) => false
    }
}

object TaskDescription {
  implicit val formatTaskDescription: Format[TaskDescription] =
    Json.format[TaskDescription]

  val empty: TaskDescription =
    TaskDescription("", "", Some(GatlingJob.empty), None)
}
