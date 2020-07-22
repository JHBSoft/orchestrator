package com.simacan.testframework.orchestrator.webhook

import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Simple container of Github WebHook data
  *
  * @param ref The reference of the push (basically containing the BranchName)
  * @param repository The fullname of the (GitHub) repository involved
  * @param rawJson The entire json message received
  */
case class GithubWebHookMessage(ref: String, repository: String, rawJson: JsObject) {

  def getRawJsonBytes: Array[Byte] = rawJson.toString().getBytes()
}

object GithubWebHookMessage {

  implicit val format: Format[GithubWebHookMessage] = (
    (JsPath \ "ref").format[String] and
      (JsPath \ "repository" \ "full_name").format[String] and
      JsPath.format[JsObject]
  )(GithubWebHookMessage.apply, unlift(GithubWebHookMessage.unapply))
}
