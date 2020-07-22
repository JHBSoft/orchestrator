package com.simacan.testframework.orchestrator.scheduler.messages

import com.simacan.testframework.orchestrator.generic.EncryptionUtils
import play.api.libs.json.{Json, OFormat}

case class EncryptKeyItem(value: String, name: Option[String]) {

  def encrypted: EncryptKeyItem =
    EncryptKeyItem(EncryptionUtils.encrypt(value), name)
}

object EncryptKeyItem {
  implicit val formatEncryptKeyItem: OFormat[EncryptKeyItem] =
    Json.format[EncryptKeyItem]
}

case class EncryptKeysRequestResponse(items: Seq[EncryptKeyItem]) {

  def encrypted: EncryptKeysRequestResponse =
    EncryptKeysRequestResponse(items.map(f => f.encrypted))
}

object EncryptKeysRequestResponse {
  implicit val formatEncryptKeysRequest: OFormat[EncryptKeysRequestResponse] =
    Json.format[EncryptKeysRequestResponse]
}
