package com.simacan.testframework.orchestrator.scheduler.tasks

import com.simacan.testframework.orchestrator.scheduler.model.{TaskDescription, TaskDescriptionFile}
import org.slf4j.LoggerFactory
import play.api.libs.json.Json

import scala.io.Source
import scala.util.{Failure, Try}

object TaskDescriptionFileLoader {
  private val logger = LoggerFactory.getLogger(getClass)

  private[scheduler] def loadSchedule(
    resourceName: String
  ): TaskDescriptionFile =
    tryGetSchedule(resourceName).getOrElse(
      TaskDescriptionFile(Seq.empty[TaskDescription], None, None)
    )

  private[scheduler] def tryGetSchedule(
    fileName: String
  ): Try[TaskDescriptionFile] =
    tryParseJsonFromString(tryGetString(fileName).getOrElse(""))

  private[scheduler] def tryGetString(fileName: String): Try[String] =
    tryGetStringFromResource(fileName).orElse(tryGetStringFromFile(fileName))

  private[scheduler] def tryGetStringFromFile(fileName: String): Try[String] = {
    Try {
      logger.info(s"Reading taskDescriptions from file: $fileName")
      val source = scala.io.Source.fromFile(fileName)
      val stream: String = try source.mkString
      finally source.close()
      logger.info(s"TaskDescriptions read from file: $fileName")
      stream
    } recoverWith {
      case e: Throwable =>
        logger.warn(s"Error reading string from file: $fileName", e)
        Failure(e)
    }
  }

  private[scheduler] def tryGetStringFromResource(
    resourceName: String
  ): Try[String] =
    Try {
      logger.info(s"Reading taskDescriptions from resource: $resourceName")
      val contentString = Source.fromResource(resourceName).getLines().mkString
      logger.info(s"TaskDescriptions read from resource: $resourceName")

      contentString

    } recoverWith {
      case e: Throwable =>
        logger.warn(s"Error reading string from resource: $resourceName", e)
        Failure(e)
    }

  private[scheduler] def tryParseJsonFromString(
    stream: String
  ): Try[TaskDescriptionFile] =
    Try {
      Json.parse(stream).as[TaskDescriptionFile]
    } recoverWith {
      case e: Throwable =>
        logger.warn(s"Error parsing TaskDescriptionFile", e)
        Failure(e)
    }

}
