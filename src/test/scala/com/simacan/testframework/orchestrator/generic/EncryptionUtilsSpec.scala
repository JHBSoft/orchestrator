package com.simacan.testframework.orchestrator.generic

import com.simacan.testframework.orchestrator.helpers.Slf4jLogTester
import com.simacan.testframework.orchestrator.scheduler.model.GatlingJobOption
import org.scalatest.TryValues
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

class EncryptionUtilsSpec
    extends AnyWordSpec
    with Matchers
    with TryValues
    with TableDrivenPropertyChecks
    with Slf4jLogTester {

  val originalMessage: String = "This is my first message to encrypt!!"

  "EncryptionUtils Encrypt / Decrypt" should {

    "return the same decrypted message as the original message" in {

      val encryptedMessage =
        EncryptionUtils.encrypt(originalMessage)
      val decryptedMessage =
        EncryptionUtils
          .decrypt(encryptedMessage)
          .getOrElse("invalid")

      encryptedMessage should not be originalMessage
      decryptedMessage shouldBe originalMessage
    }

    "return proper result" in {
      // EncryptionKey used: ENCRYPTION_KEY=0102030405060708090a0b0c0d0e0f10
      EncryptionUtils
        .decrypt(
          "v1:6a529e9e709f6e235eba495b13b37c24:d5bf1c7300e0f37f2e7b3f85ba26b6ecc4d6b056a0269b0d59a14da871a6e32b1a0199be692669ea7272de38d031ce14"
        )
        .success
        .get shouldBe originalMessage
    }

    "return error message" in {
      val keys = Table(
        ("data", "message"),
        (
          // Invalid version error
          "v2:6a529e9e709f6e235eba495b13b37c24:d5bf1c7300e0f37f2e7b3f85ba26b6ecc4d6b056a0269b0d59a14da871a6e32b1a0199be692669ea7272de38d031ce14",
          "Invalid encrypted string"
        ),
        (
          // Error message with wrong iv-part"
          "v1:6a529e9e709f6e235eba495b13b37c:d5bf1c7300e0f37f2e7b3f85ba26b6ecc4d6b056a0269b0d59a14da871a6e32b1a0199be692669ea7272de38d031ce14",
          "initialisation vector must be the same length as block size"
        ),
        (
          // Incomplete message block (valid hexcode)
          "v1:6a529e9e709f6e235eba495b13b37c24:d5bf1c7300e0f37f2e7b3f85ba26b6ecc4d6b056a0269b0d59a14da871a6e32b1a0199be692669ea7272de38d031ce",
          "last block incomplete in decryption"
        ),
        (
          // "Invalid hexcode in messageblock"
          "v1:6a529e9e709f6e235eba495b13b37c24:d5bf1c7300e0f37f2e7b3f85ba26b6ecc4d6b056a0269b0d59a14da871a6e32b1a0199be692669ea7272de38d031ce1",
          "exception decoding Hex"),
        (
          // non valid hexcode
          "v1:6a529e9e709f6e235eba495b13b37c24:NonValidHexCode",
          "exception decoding Hex"),
        (
          // Empty string
          "",
          "Invalid encrypted string")
      )

      val testLogger = TestLoggerFactory.getLogger(EncryptionUtils.getClass)

      forAll(keys) { (data, message) =>
        EncryptionUtils.decrypt(data).failure.exception.getMessage should include(message)
        testLogger.logsList.size shouldBe 1
        testLogger.logsList.head.getMessage shouldBe s"Decryption failed of: ${GatlingJobOption.maskData(data)}"
        testLogger.logsList.head.getThrowableProxy.getMessage should include(message)
        testLogger.clearList()
      }
    }
  }

  "EncryptionUtils.validateEncryptionKey" should {
    "return valid result with input key" in {
      val keys = Table(
        ("key", "validResult"),
        ("abcdefghijklmnopqrstuvwxyzabcdef", true),
        ("a1#defghijklmnopqrstuvwxyzabcdef", true),
        ("abcdefghijklmnopqrstuvwxyzabcdefg", false),
        ("abcdefghijklmnopqrstuvwxyzabcde", false),
        ("abc", false)
      )

      forAll(keys) { (key, validResult) =>
        EncryptionUtils.validateEncryptionKey(key) shouldBe validResult
      }
    }
  }
}
