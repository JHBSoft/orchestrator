package com.simacan.testframework.orchestrator.scheduler.model

import play.api.libs.json.{Format, Json}

case class GatlingJob(
  script: String,
  description: Option[String],
  jobTemplate: Option[String],
  jobQueue: Option[String],
  options: Option[Seq[GatlingJobOption]],
  containerOptions: Option[ContainerOptions]) {

  def isEmpty: Boolean = this.equals(GatlingJob.empty)
}

object GatlingJob {
  implicit val formatGatlingJob: Format[GatlingJob] = Json.format[GatlingJob]

  val empty: GatlingJob =
    GatlingJob("", None, None, None, Some(Seq.empty[GatlingJobOption]), None)
}
