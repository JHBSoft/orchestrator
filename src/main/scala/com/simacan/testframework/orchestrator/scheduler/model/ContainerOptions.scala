package com.simacan.testframework.orchestrator.scheduler.model
import play.api.libs.json.{Format, Json}

case class ContainerOptions(vCpu: Option[Int], memory: Option[Int]) {

  def isEmpty: Boolean = this.equals(ContainerOptions.empty)
}

object ContainerOptions {
  implicit val formatContainerOptions: Format[ContainerOptions] = Json.format[ContainerOptions]

  val empty: ContainerOptions = ContainerOptions(None, None)
}
