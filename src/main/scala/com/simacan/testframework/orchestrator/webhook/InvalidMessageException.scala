package com.simacan.testframework.orchestrator.webhook

final case class InvalidMessageException(private val message: String) extends Exception(message)
