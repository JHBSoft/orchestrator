package com.simacan.testframework.orchestrator.generic

import java.security.{SecureRandom, Security}

import com.simacan.testframework.orchestrator.config.AppConfigLoader
import com.simacan.testframework.orchestrator.scheduler.model.GatlingJobOption
import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.{KeyParameter, ParametersWithIV}
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import org.slf4j.LoggerFactory

import scala.util.{Failure, Try}

object EncryptionUtils {
  Security.addProvider(new BouncyCastleProvider())

  private[this] val encoding: String = "UTF-8"
  private[this] val random = new SecureRandom()
  private[this] val encryptionKey: String =
    AppConfigLoader.loaded.config.scheduler.encryptionKey

  def validateEncryptionKey(encryptionKey: String): Boolean = {
    encryptionKey.length == 32
  }

  /***
    * Encrypts the given data string to a format:
    * encryptionVersion:ivKey:encryptedData
    *
    * Remarks:
    * The encryption key is retrieved from the configuration settings
    * The ivKey is generated randomly and added to the encrypted string
    *
    * @param data string to encrypt
    * @return the encrypted string
    */
  def encrypt(data: String): String = {
    val keyBytes = encryptionKey.getBytes(encoding)
    val aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()))
    val iv = random.generateSeed(16)
    val ivAndKey = new ParametersWithIV(new KeyParameter(keyBytes), iv)
    aes.init(true, ivAndKey)

    val encrypted = cipherData(aes, data.getBytes(encoding))

    val encodedString = new String(Hex.encode(encrypted), encoding)
    val ivString = new String(Hex.encode(iv), encoding)

    // Return the encrypted data in a format so the decryption can decrypt.
    s"v1:$ivString:$encodedString"
  }

  /***
    * Decrypts a given encrypted string.

    * The format of the encrypted string is:
    * encryptionVersion:ivKey:encryptedData
    *
    * Remarks:
    * The encryption key is retrieved from the configuration settings
    * The ivKey is retrieved from the encrypted string
    *
    * @param data string containing the encrypted data
    * @return the unencrypted string
    */
  def decrypt(data: String): Try[String] = {
    Try {
      val keyBytes = encryptionKey.getBytes(encoding)
      // Split the data into its iv and encrypted data.
      data.split(':').toList match {
        case "v1" :: iv :: encryptedData :: Nil =>
          decryptVersion1(iv, encryptedData, keyBytes)
        case _ =>
          throw new RuntimeException("Invalid encrypted string")
      }
    } recoverWith {
      case e: Throwable =>
        LoggerFactory.getLogger(getClass).warn(s"Decryption failed of: ${GatlingJobOption.maskData(data)}", e)
        Failure(e)
    }
  }

  private[this] def cipherData(cipher: PaddedBufferedBlockCipher, data: Array[Byte]): Array[Byte] = {
    val minSize = cipher.getOutputSize(data.length)
    val outBuf = new Array[Byte](minSize)
    val length1 = cipher.processBytes(data, 0, data.length, outBuf, 0)
    val length2 = cipher.doFinal(outBuf, length1)
    val actualLength = length1 + length2

    // remove padding
    val result = new Array[Byte](actualLength)
    System.arraycopy(outBuf, 0, result, 0, actualLength)
    result
  }

  private[this] def decryptVersion1(iv: String, encryptedData: String, keyBytes: Array[Byte]): String = {
    val aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()))
    val ivAndKey = new ParametersWithIV(
      new KeyParameter(keyBytes),
      Hex.decode(iv.getBytes(encoding))
    )
    aes.init(false, ivAndKey)
    val decrypted =
      cipherData(aes, Hex.decode(encryptedData.getBytes(encoding)))
    new String(decrypted, encoding)
  }

}
