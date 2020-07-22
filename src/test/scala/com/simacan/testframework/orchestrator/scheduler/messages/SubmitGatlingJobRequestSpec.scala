package com.simacan.testframework.orchestrator.scheduler.messages

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import com.simacan.testframework.orchestrator.scheduler.model.{GatlingJob, TaskDescription}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class SubmitGatlingJobRequestSpec extends AnyWordSpec with Matchers {

  "SubmitGatlingJobRequestSpec.getDefaultTaskName" should {

    "have a default name" in {
      SubmitGatlingJobRequest.getDefaultTaskName should startWith("Task")
    }

    "have the current time" in {
      SubmitGatlingJobRequest.getDefaultTaskName should include(
        OffsetDateTime
          .now()
          .format(DateTimeFormatter.ofPattern("YYYY-MM-dd_HHmmss"))
      )
    }
  }

  "convertToTaskDescription" should {

    "return a TaskDescription with Default name" in {
      val taskDescription: Option[String] => TaskDescription =
        SubmitGatlingJobRequest(GatlingJob.empty, _).convertToTaskDescription()

      taskDescription(None).name should startWith("Task")
      taskDescription(Some("test")).name shouldBe "test"
    }
  }
}
