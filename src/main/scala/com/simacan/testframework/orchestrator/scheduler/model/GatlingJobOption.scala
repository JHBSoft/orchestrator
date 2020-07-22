package com.simacan.testframework.orchestrator.scheduler.model

import com.simacan.testframework.orchestrator.generic.EncryptionUtils
import play.api.libs.json.{Format, Json}

case class GatlingJobOption(name: String, value: String, encrypted: Option[Boolean]) {

  def getValue: String =
    if (encrypted.getOrElse(false))
      EncryptionUtils.decrypt(value).getOrElse("")
    else value

  def isEmpty: Boolean = this.equals(GatlingJobOption.empty)

  override def toString: String = {
    val valueString: String = if (encrypted.getOrElse(false)) GatlingJobOption.maskData(value) else value
    s"GatlingJobOption($name;$valueString)"
  }
}

object GatlingJobOption {
  implicit val formatGatlingJobOption: Format[GatlingJobOption] =
    Json.format[GatlingJobOption]

  val empty: GatlingJobOption = GatlingJobOption("", "", None)

  def maskData(data: String, partSize: Int = 6): String = {
    data
      .substring(0, Math.min(data.length, partSize))
      .concat("*" * 12)
      .concat(data.substring(Math.max(0, data.length - partSize)))
  }

}
