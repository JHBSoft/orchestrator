package com.simacan.testframework.orchestrator.scheduler.tasks

import com.simacan.testframework.orchestrator.helpers.Slf4jLogTester
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class TaskDescriptionFileLoaderSpec extends AnyWordSpec with Matchers with Slf4jLogTester {

  val jobScheduleFile: String =
    getClass.getResource("/jobSchedule.json").getPath

  "tryGetString Methods" should {

    "return equal string" in {
      val resource =
        TaskDescriptionFileLoader
          .tryGetStringFromResource("jobSchedule.json")
          .get
      val filestr = TaskDescriptionFileLoader
        .tryGetStringFromFile(jobScheduleFile)
        .get
        .replace("\n", "")

      //remove the \n characters due to difference in reading
      resource shouldEqual filestr
    }

    "be a failure with improper file name" in {

      val testLogger = TestLoggerFactory.getLogger(TaskDescriptionFileLoader.getClass)
      TaskDescriptionFileLoader
        .tryGetStringFromFile("jobSchedule.json")
        .isFailure shouldBe true

      testLogger.logsList.size shouldBe 2
      testLogger.logsList.head.getMessage shouldBe "Reading taskDescriptions from file: jobSchedule.json"
      testLogger.logsList(1).getMessage shouldBe "Error reading string from file: jobSchedule.json"
    }

    "be successful (resource)" in {
      val testLogger = TestLoggerFactory.getLogger(TaskDescriptionFileLoader.getClass)

      TaskDescriptionFileLoader
        .tryGetString("jobSchedule.json")
        .isSuccess shouldBe true

      testLogger.logsList.size shouldBe 2
      testLogger.logsList.head.getMessage shouldBe "Reading taskDescriptions from resource: jobSchedule.json"
      testLogger.logsList(1).getMessage shouldBe "TaskDescriptions read from resource: jobSchedule.json"
    }

    "be successful (file)" in {
      val testLogger = TestLoggerFactory.getLogger(TaskDescriptionFileLoader.getClass)

      TaskDescriptionFileLoader
        .tryGetString(jobScheduleFile)
        .isSuccess shouldBe true

      testLogger.logsList.size shouldBe 4
      testLogger.logsList.head.getMessage shouldBe s"Reading taskDescriptions from resource: $jobScheduleFile"
      testLogger.logsList(1).getMessage shouldBe s"Error reading string from resource: $jobScheduleFile"
      testLogger.logsList(2).getMessage shouldBe s"Reading taskDescriptions from file: $jobScheduleFile"
      testLogger.logsList(3).getMessage shouldBe s"TaskDescriptions read from file: $jobScheduleFile"
    }

    "be a failure" in {
      val testLogger = TestLoggerFactory.getLogger(TaskDescriptionFileLoader.getClass)

      TaskDescriptionFileLoader
        .tryGetString("jobSchedule.json.not-existing")
        .isFailure shouldBe true

      testLogger.logsList.size shouldBe 4
      testLogger.logsList.head.getMessage shouldBe "Reading taskDescriptions from resource: jobSchedule.json.not-existing"
      testLogger.logsList(1).getMessage shouldBe "Error reading string from resource: jobSchedule.json.not-existing"
      testLogger.logsList(2).getMessage shouldBe "Reading taskDescriptions from file: jobSchedule.json.not-existing"
      testLogger.logsList(3).getMessage shouldBe "Error reading string from file: jobSchedule.json.not-existing"
    }

  }

  "tryGetJsonFromString" should {

    "be a not successful in arbitrary json file" in {
      TaskDescriptionFileLoader
        .tryParseJsonFromString("[{\"people\":\"first\"}]")
        .isSuccess shouldBe false
    }

    "be successful" in {
      TaskDescriptionFileLoader
        .tryParseJsonFromString(
          TaskDescriptionFileLoader.tryGetStringFromFile(jobScheduleFile).get
        )
        .isSuccess shouldBe true
    }

    "have equal results for parsing from file and parsing from resource" in {
      val jsonFile = TaskDescriptionFileLoader
        .tryParseJsonFromString(
          TaskDescriptionFileLoader.tryGetStringFromFile(jobScheduleFile).get
        )
      val jsonResource =
        TaskDescriptionFileLoader
          .tryParseJsonFromString(
            TaskDescriptionFileLoader
              .tryGetStringFromResource("jobSchedule.json")
              .get
          )

      jsonFile shouldEqual jsonResource
    }
  }

}
