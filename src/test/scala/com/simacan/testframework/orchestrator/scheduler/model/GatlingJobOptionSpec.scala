package com.simacan.testframework.orchestrator.scheduler.model
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpec

class GatlingJobOptionSpec extends AnyWordSpec with Matchers with TableDrivenPropertyChecks {

  "maskData" should {
    "return a masked string" in {

      val dataTable = Table(
        ("data", "masked"),
        ("Help me throughout the winter", "Help m************winter"),
        ("Help", "Help************Help")
      )
      forAll(dataTable) { (data, masked) =>
        GatlingJobOption.maskData(data) shouldBe masked
      }
    }
  }

  "GatlingJobOption toString" should {
    "return a readable string" in {

      val dataTable = Table(
        ("key", "value", "encrypted", "stringOutput"),
        ("keyname", "keyvalue", false, "GatlingJobOption(keyname;keyvalue)"),
        ("keyname", "keyvalue", true, "GatlingJobOption(keyname;keyval************yvalue)")
      )

      forAll(dataTable) { (keyName, keyValue, encrypted, stringOutput) =>
        GatlingJobOption(keyName, keyValue, Option(encrypted)).toString shouldBe stringOutput
      }
    }
  }

}
