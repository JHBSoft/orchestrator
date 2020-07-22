package com.simacan.testframework.orchestrator.scheduler.model

import org.scalatest.TryValues
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class TaskDescriptionSpec extends AnyWordSpec with Matchers with TryValues {

  "An empty TaskDescription" should {

    "be empty" in {
      TaskDescription.empty.isEmpty shouldBe true
    }

    "be empty with created TaskDescription" in {
      TaskDescription("", "", Some(GatlingJob.empty), None).isEmpty shouldBe true
    }
  }

  "An empty GatlingJob" should {
    "be empty" in {
      GatlingJob.empty.isEmpty shouldBe true
    }

    "be empty with created GatlingJob" in {
      GatlingJob("", None, None, None, Some(Seq.empty[GatlingJobOption]), None).isEmpty shouldBe true
    }
  }

  "An empty GatlingJobOption" should {
    "be empty" in {
      GatlingJobOption.empty.isEmpty shouldBe true
    }

    "be empty with created GatlingJobOption" in {
      GatlingJobOption("", "", None).isEmpty shouldBe true
    }

    "return an empty string" in {
      GatlingJobOption.empty.getValue shouldBe ""
    }
  }

  "A filled GatlingJob" should {
    "be non empty" in {
      GatlingJob(
        "hi",
        Some(""),
        Some(""),
        Some(""),
        Some(Seq.empty[GatlingJobOption]),
        Some(ContainerOptions.empty)
      ).isEmpty shouldBe false
    }
  }

  "A ContainerOption" should {
    "be empty" in {
      ContainerOptions.empty.isEmpty shouldBe true
    }

    "be empty with created ContainerOption" in {
      ContainerOptions(None, None).isEmpty shouldBe true
    }

    "be non Empty while filled" in {
      ContainerOptions(Some(1), Some(5)).isEmpty shouldBe false
    }
  }

  "A filled TaskDescription" should {
    "be non empty" in {
      TaskDescription("name", "cron", Some(GatlingJob.empty), Some("")).isEmpty shouldBe false
    }

    "be non empty (2)" in {
      TaskDescription.empty
        .copy(gatlingJob = Some(GatlingJob.empty.copy(script = "test")))
        .isEmpty shouldBe false
    }
  }

  "A filled GatlingJobOption" should {
    "return value if encrypted not set" in {
      GatlingJobOption("demo", "value", None).getValue shouldBe "value"
    }

    "return value if encrypted set to false" in {
      GatlingJobOption("demo", "value", Some(false)).getValue shouldBe "value"
    }

    "return invalid value if encrypted set to true" in {
      GatlingJobOption("demo", "value", Some(true)).getValue shouldBe ""
    }

    "return proper value if encrypted set to true" in {
      // EncryptionKey used: ENCRYPTION_KEY=0102030405060708090a0b0c0d0e0f10
      GatlingJobOption(
        "demo",
        "v1:6a529e9e709f6e235eba495b13b37c24:d5bf1c7300e0f37f2e7b3f85ba26b6ecc4d6b056a0269b0d59a14da871a6e32b1a0199be692669ea7272de38d031ce14",
        Some(true)
      ).getValue shouldBe "This is my first message to encrypt!!"

    }
  }

  "hasValidCron" should {
    "should be successful" in {
      TaskDescription("validTask", "* */5 * * * ? *", None, None).hasValidCron shouldBe true
    }

    "should be failure" in {
      TaskDescription("invalidTask", "* */5 * * * * *", None, None).hasValidCron shouldBe false
    }
  }
}
