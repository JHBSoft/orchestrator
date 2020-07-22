package com.simacan.testframework.orchestrator.scheduler.messages

import com.simacan.testframework.orchestrator.generic.EncryptionUtils
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class EncryptKeysRequestResponseSpec extends AnyWordSpec with Matchers {

  "EncryptKeyItem.encrypt" should {
    "return an encrypted item" in {
      val encryptKeyItem: EncryptKeyItem =
        EncryptKeyItem(value = "toEncrypt", Some("value")).encrypted

      EncryptionUtils.decrypt(encryptKeyItem.value).get shouldBe "toEncrypt"
      encryptKeyItem.name.get shouldBe "value"
    }
  }

  "EncryptKeysRequestResponse.encrypt" should {
    "return all items encrypted" in {
      val encrypted: EncryptKeysRequestResponse = EncryptKeysRequestResponse(
        Seq(
          EncryptKeyItem("toEncrypt1", Some("value1")),
          EncryptKeyItem("toEncrypt2", Some("value2"))
        )
      ).encrypted

      EncryptionUtils
        .decrypt(encrypted.items.head.value)
        .get shouldBe "toEncrypt1"
      EncryptionUtils
        .decrypt(encrypted.items(1).value)
        .get shouldBe "toEncrypt2"

    }
  }
}
