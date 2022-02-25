/*
 * Copyright 2014 Frugal Mechanic (http://frugalmechanic.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fm.common

import java.nio.charset.StandardCharsets.UTF_8
import java.security.{InvalidAlgorithmParameterException, MessageDigest, SecureRandom}
import java.util.Arrays
import javax.crypto.spec.{GCMParameterSpec, IvParameterSpec, PBEKeySpec, SecretKeySpec}
import javax.crypto._

/**
 *
 *
 * NOTE: Use at your own risk.  We make no claim that any of this Crypto code is correct.
 */
object Crypto {
  private val DefaultKeyLengthBits: Int = 256

  object PBKDF2 {
    private def PBKDF2_HMAC_SHA256: String = "PBKDF2WithHmacSHA256"

    /** PBKDF2-HMAC-SHA256 with the result encoded as a HEX string */
    def sha256Hex(salt: Array[Byte], password: Array[Char], iterationCount: Int): String = {
      Base16.encode(sha256(salt, password, iterationCount))
    }

    /** PBKDF2-HMAC-SHA256 with the result encoded as a HEX string */
    def sha256Hex(salt: Array[Byte], password: String, iterationCount: Int): String = {
      Base16.encode(sha256(salt, password, iterationCount))
    }

    /** PBKDF2-HMAC-SHA256 */
    def sha256(salt: Array[Byte], password: String, iterationCount: Int): Array[Byte] = {
      sha256(salt, password.toCharArray, iterationCount)
    }

    /** PBKDF2-HMAC-SHA256 */
    def sha256(salt: Array[Byte], password: Array[Char], iterationCount: Int): Array[Byte] = {
      sha256(new PBEKeySpec(password, salt, iterationCount, 256))
    }

    private def sha256(spec: PBEKeySpec): Array[Byte] = {
      val factory: SecretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_HMAC_SHA256)
      factory.generateSecret(spec).getEncoded()
    }
  }

  def makeRandomKeyBase64(): String = makeRandomKeyBase64(DefaultKeyLengthBits, urlSafe = false)

  def makeRandomKeyBase64URLSafe(): String = makeRandomKeyBase64(DefaultKeyLengthBits, urlSafe = true)

  def makeRandomKeyBase64(bits: Int): String = makeRandomKeyBase64(bits, urlSafe = false)

  def makeRandomKeyBase64URLSafe(bits: Int): String = makeRandomKeyBase64(bits, urlSafe = true)

  def makeRandomKeyBase64(bits: Int, urlSafe: Boolean): String = base64Encode(makeRandomKey(bits), urlSafe)

  def makeRandomKey(bits: Int): Array[Byte] = {
    require(bits % 8 === 0, "bits should be a multiple of 8")
    val bytes = new Array[Byte](bits / 8)
    new SecureRandom().nextBytes(bytes)
    bytes
  }

  def main(args: Array[String]): Unit = {
    var bits: Int = DefaultKeyLengthBits
    var urlSafe: Boolean = false

    args.foreach{ (arg: String) =>
      if (arg.isInt) bits = arg.toInt
      else if (arg.isBoolean) urlSafe = arg.toBoolean
      else throw new IllegalArgumentException("Invalid Argument: "+arg)
    }

    println("Generated Base64 Key: "+makeRandomKeyBase64(bits, urlSafe))
  }

  def defaultCipherForRawKey(key: Array[Byte]): Crypto = new Crypto(key, new DefaultCipher)

  def defaultCipherForBase64Key(key: String): Crypto = new Crypto(base64Decode(key), new DefaultCipher)

  def authenticatedCipherForRawKey(key: Array[Byte]): Crypto = new Crypto(key, new AuthenticatedCipher)

  def authenticatedCipherForBase64Key(key: String): Crypto = new Crypto(base64Decode(key), new AuthenticatedCipher)

  sealed trait CipherMode {
    protected val cipher: Cipher
    def init(mode: Int, keyBytes: Array[Byte], iv: Array[Byte]): Unit

    final def getBlockSize: Int = cipher.getBlockSize()
    final def doFinal(in: Array[Byte]): Array[Byte] = cipher.doFinal(in)
  }

  final class DefaultCipher extends CipherMode {
    protected val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    def init(mode: Int, keyBytes: Array[Byte], iv: Array[Byte]): Unit = {
      cipher.init(mode, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(iv))
    }
  }

  final class AuthenticatedCipher extends CipherMode {
    // AES/GCM/NoPadding
    protected val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")

    def init(mode: Int, keyBytes: Array[Byte], iv: Array[Byte]): Unit = {
      cipher.init(mode, new SecretKeySpec(keyBytes, "AES"), new GCMParameterSpec(16 * java.lang.Byte.SIZE, iv))
    }
  }

  private def base64Decode(s: String): Array[Byte] = Base64.decode(s)

  private def base64Encode(bytes: Array[Byte], urlSafe: Boolean): String = {
    val encoder: BaseEncoding = if (urlSafe) Base64URL else Base64Strict
    encoder.encode(bytes)
  }

}

/**
 * A Simple Crypto Class
 *
 * NOTE: Use at your own risk.  We make no claim that any of this Crypto code is correct.
 */
final class Crypto private (rawKey: Array[Byte], cipher: Crypto.CipherMode) extends Logging {
  import Crypto.{base64Decode, base64Encode}

  private[this] def HmacSHA1: String = "HmacSHA1"
  private[this] val DefaultMac: Mac = Mac.getInstance(HmacSHA1)

  private[this] val keyLengthBits: Int = Crypto.DefaultKeyLengthBits
  private[this] val mac: Mac = DefaultMac
  private[this] val secureRandom: SecureRandom = new SecureRandom()

  private[this] val keyLenBytes: Int = {
    require(keyLengthBits % 8 === 0, "keyLengthBits should be a multiple of 8")
    keyLengthBits / 8
  }

  private[this] val keyBytes: Array[Byte] = {
    if(rawKey.length === keyLenBytes) {
      rawKey
    } else if(rawKey.length > keyLenBytes) {
      logger.warn(s"Key is too long (${rawKey.length * 8} bits).  It is being truncated to $keyLengthBits")
      rawKey.slice(0, keyLenBytes) // truncate
    } else {
      logger.warn(s"Key too short (${rawKey.length * 8} bits).  Using sha256 to expand it")
      require(keyLengthBits === 256, s"Can't expand using sha256 since key is not 256 bits.  Key is $keyLengthBits bits.")
      // NOTE: this needs to be replaced with a proper key derivation function but we first need to figure out if any
      //       production code relies on this functionality. Some tests in MessageCrypto rely on it but I don't think
      //       any production code does.
      sha256(rawKey)
    }

  }

  def encryptBase64String(plaintext: String): String = encryptBase64String(plaintext, urlSafe = false)

  def encryptBase64(bytes: Array[Byte]): String = encryptBase64(bytes, urlSafe = false)

  def encryptBase64StringURLSafe(plaintext: String): String = encryptBase64String(plaintext, urlSafe = true)

  def encryptBase64URLSafe(bytes: Array[Byte]): String = encryptBase64(bytes, urlSafe = true)

  def encryptBase64String(plaintext: String, urlSafe: Boolean): String = {
    encryptBase64(plaintext.getBytes(UTF_8), urlSafe)
  }

  def encryptBase64(bytes: Array[Byte], urlSafe: Boolean): String = base64Encode(encrypt(bytes), urlSafe)

  /** Encrypt bytes returning the iv and ciphertext combined into a single byte array (iv followed by the cipher text) */
  def encrypt(plaintext: Array[Byte]): Array[Byte] = {
    val (iv, ciphertext) = encryptRaw(plaintext)
    val bytes = new Array[Byte](iv.length + ciphertext.length)
    System.arraycopy(iv, 0, bytes, 0, iv.length)
    System.arraycopy(ciphertext, 0, bytes, iv.length, ciphertext.length)
    bytes
  }

  /** Encrypt a string returning the tuple: (iv, ciphertext) */
  def encryptRaw(plaintext: String): (Array[Byte], Array[Byte]) = {
    require(null != plaintext, "Plaintext is null!")
    encryptRaw(plaintext.getBytes(UTF_8))
  }

  /** Encrypt Bytes returning the tuple: (iv, ciphertext) */
  def encryptRaw(plaintext: Array[Byte]): (Array[Byte], Array[Byte]) = {
    val iv: Array[Byte] = new Array[Byte](cipher.getBlockSize)
    secureRandom.nextBytes(iv)
    val ciphertext: Array[Byte] = doCipher(Cipher.ENCRYPT_MODE, iv, plaintext)
    (iv, ciphertext)
  }

  /**
   * Attempt to decrypt a string encrypted using encryptBase64String()
   *
   * If successful then Some(plaintext) will be returned.  Otherwise None will be returned.
   */
  def tryDecryptBase64String(base64IvAndCiphertext: String): Option[String] = {
    tryWrap{ decryptBase64String(base64IvAndCiphertext) }
  }

  def tryDecrypt(ivAndCiphertext: Array[Byte]): Option[Array[Byte]] = tryWrap{ decrypt(ivAndCiphertext) }

  def tryDecrypt(iv: Array[Byte], ciphertext: Array[Byte]): Option[Array[Byte]] = tryWrap{ decrypt(iv, ciphertext) }

  /** Decrypt a string encrypted using encryptBase64() */
  def decryptBase64(base64IvAndCiphertext: String): Array[Byte] = {
    require(null != base64IvAndCiphertext, "Null base64IvAndCiphertext parameter")
    decrypt(base64Decode(base64IvAndCiphertext))
  }

  /** Decrypt a string encrypted using encryptBase64String() */
  def decryptBase64String(base64IvAndCiphertext: String): String = {
    val plaintextBytes: Array[Byte] = decryptBase64(base64IvAndCiphertext)
    new String(plaintextBytes, UTF_8)
  }

  /** Decrypt given the combined IV and Ciphertext */
  def decrypt(ivAndCiphertext: Array[Byte]): Array[Byte] = {
    val iv: Array[Byte] = Arrays.copyOfRange(ivAndCiphertext, 0, cipher.getBlockSize)
    val ciphertext: Array[Byte] = Arrays.copyOfRange(ivAndCiphertext, cipher.getBlockSize, ivAndCiphertext.length)
    doCipher(Cipher.DECRYPT_MODE, iv, ciphertext)
  }

  /** Decrypt given the IV and Ciphertext */
  def decrypt(iv: Array[Byte], ciphertext: Array[Byte]): Array[Byte] = doCipher(Cipher.DECRYPT_MODE, iv, ciphertext)

  @inline private def tryWrap[T](f: => T): Option[T] = {
    try {
      Some(f)
    } catch {
      case _: IllegalArgumentException => None
      case _: java.io.IOException => None // fm.common.Base64.decode throws an IOException
      case _: AEADBadTagException => None // Note: This is a subtype of BadPaddingException so it is probably redundant
      case _: BadPaddingException => None
      case _: IllegalBlockSizeException => None
      case _: InvalidAlgorithmParameterException => None
      case e: java.security.ProviderException => None // A ShortBufferException wrapped in a ProviderException is thrown starting in Java 17. Let's just catch all ProviderExceptions
      case _: ShortBufferException => None // So far this only shows up wrapped in a ProviderException but let's also catch the unwrapped exception in case something changes
    }
  }

  def macBase64(data: String): String = macBase64(data.getBytes(UTF_8))
  def macBase64URLSafe(data: String): String = macBase64URLSafe(data.getBytes(UTF_8))
  def macBase64(data: String, urlSafe: Boolean): String = macBase64(data.getBytes(UTF_8), urlSafe = urlSafe)

  /** The Base64 Encoded MAC for an array of bytes */
  def macBase64(data: Array[Byte]): String = macBase64(data, urlSafe = false)

  def macBase64URLSafe(data: Array[Byte]): String = macBase64(data, urlSafe = true)

  def macBase64(data: Array[Byte], urlSafe: Boolean): String = base64Encode(mac(data), urlSafe)

  /** The Hex Encoded MAC for a String */
  def macHex(data: String): String = macHex(data.getBytes(UTF_8))

  /** The Hex Encoded MAC for an array of bytes */
  def macHex(data: Array[Byte]): String = Base16.encode(mac(data))

  /** Calculate the MAC for an array of bytes */
  def mac(data: Array[Byte]): Array[Byte] = mac.synchronized {
    mac.init(new SecretKeySpec(keyBytes, HmacSHA1))
    mac.doFinal(data)
  }

  private def sha256(data: Array[Byte]): Array[Byte] = {
    MessageDigest.getInstance("SHA-256").digest(data)
  }

  private def doCipher(mode: Int, iv: Array[Byte], data: Array[Byte]): Array[Byte] = cipher.synchronized {
    cipher.init(mode, keyBytes, iv)
    cipher.doFinal(data)
  }
}