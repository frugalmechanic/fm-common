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
import java.security.SecureRandom
import java.util.Arrays
import org.bouncycastle.crypto.{BufferedBlockCipher, Digest, Mac}
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.digests.{SHA1Digest, SHA256Digest}
import org.bouncycastle.crypto.engines.AESFastEngine
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.bouncycastle.crypto.modes.{CBCBlockCipher, GCMBlockCipher}
import org.bouncycastle.crypto.paddings.{PaddedBufferedBlockCipher, PKCS7Padding}
import org.bouncycastle.crypto.params.{KeyParameter, ParametersWithIV}

/**
 * 
 * 
 * NOTE: Use at your own risk.  We make no claim that any of this Crypto code is correct.
 */
object Crypto {
  private val DefaultKeyLengthBits: Int = 256
  
  object PBKDF2 {
    /** PBKDF2-HMAC-SHA256 */
    def sha256(salt: Array[Byte], password: String, iterationCount: Int): Array[Byte] = sha256(salt, password.getBytes(UTF_8), iterationCount)
    
    /** PBKDF2-HMAC-SHA256 */
    def sha256(salt: Array[Byte], password: Array[Byte], iterationCount: Int): Array[Byte] = {
      val gen: PKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator(new SHA256Digest())
      gen.init(password, salt, iterationCount)
      val dk: Array[Byte] = gen.generateDerivedParameters(256).asInstanceOf[KeyParameter].getKey()
      dk
    }
    
    /** PBKDF2-HMAC-SHA256 with the result encoded as a HEX string */
    def sha256Hex(salt: Array[Byte], password: Array[Byte], iterationCount: Int): String = Hex.encodeHexString(sha256(salt, password, iterationCount))
    
    /** PBKDF2-HMAC-SHA256 with the result encoded as a HEX string */
    def sha256Hex(salt: Array[Byte], password: String, iterationCount: Int): String = Hex.encodeHexString(sha256(salt, password, iterationCount))
  }
  
  def makeRandomKeyBase64(): String = makeRandomKeyBase64(DefaultKeyLengthBits, urlSafe = false)
  
  def makeRandomKeyBase64URLSafe(): String = makeRandomKeyBase64(DefaultKeyLengthBits, urlSafe = true)
  
  def makeRandomKeyBase64(bits: Int): String = makeRandomKeyBase64(bits, urlSafe = false)
  
  def makeRandomKeyBase64URLSafe(bits: Int): String = makeRandomKeyBase64(bits, urlSafe = true)
  
  def makeRandomKeyBase64(bits: Int, urlSafe: Boolean): String = Base64.encodeBytes(makeRandomKey(bits), if (urlSafe) Base64.URL_SAFE else Base64.NO_OPTIONS)
  
  def makeRandomKey(bits: Int): Array[Byte] = {
    require(bits % 8 == 0, "bits should be a multiple of 8")
    val bytes = new Array[Byte](bits / 8)
    new SecureRandom().nextBytes(bytes)
    bytes
  }
  
  def main(args: Array[String]): Unit = {
    var bits: Int = DefaultKeyLengthBits
    var urlSafe: Boolean = false
    
    args.foreach{ arg: String =>
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

  sealed trait Cipher {
    def getBlockSize: Int
    def init(forEncryption: Boolean, keyBytes: Array[Byte], iv: Array[Byte]): Unit
    def getOutputSize(len: Int): Int
    def processBytes(in: Array[Byte], inOff: Int, len: Int, out: Array[Byte], outOff: Int): Int
    def doFinal(out: Array[Byte], outOff: Int): Int
  }
  
  final class DefaultCipher extends Cipher {
    private[this] val cipher: BufferedBlockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine), new PKCS7Padding)
    
    def getBlockSize: Int = cipher.getBlockSize()
    def init(forEncryption: Boolean, keyBytes: Array[Byte], iv: Array[Byte]): Unit = cipher.init(forEncryption, new ParametersWithIV(new KeyParameter(keyBytes), iv))
    def getOutputSize(len: Int): Int = cipher.getOutputSize(len)
    def processBytes(in: Array[Byte], inOff: Int, len: Int, out: Array[Byte], outOff: Int): Int = cipher.processBytes(in, inOff, len, out, outOff)
    def doFinal(out: Array[Byte], outOff: Int): Int = cipher.doFinal(out, outOff)
  }
  
  final class AuthenticatedCipher extends Cipher {
    private[this] val aes: AESFastEngine = new AESFastEngine
    private[this] val cipher: GCMBlockCipher = new GCMBlockCipher(aes)
    
    def getBlockSize: Int = aes.getBlockSize()
    def init(forEncryption: Boolean, keyBytes: Array[Byte], iv: Array[Byte]): Unit = cipher.init(forEncryption, new ParametersWithIV(new KeyParameter(keyBytes), iv))
    def getOutputSize(len: Int): Int = cipher.getOutputSize(len)
    def processBytes(in: Array[Byte], inOff: Int, len: Int, out: Array[Byte], outOff: Int): Int = cipher.processBytes(in, inOff, len, out, outOff)
    def doFinal(out: Array[Byte], outOff: Int): Int = cipher.doFinal(out, outOff)
  }
  
  // I'm not sure why the base64 decoders don't automatically decode the URL-safe variant...
  private def base64Decode(s: String): Array[Byte] = {
    Base64.decode(s, base64DecodeMode(s))
  }
  
  // Detect if we need to pass the URL_SAFE flag
  private def base64DecodeMode(s: String): Int = {
    var i: Int = 0
    while (i < s.length) {
      (s.charAt(i): @scala.annotation.switch) match {
        case '-' | '_' => return Base64.URL_SAFE
        case '+' | '/' => return Base64.NO_OPTIONS
        case _ =>
      }
      i += 1
    }
    
    return Base64.NO_OPTIONS
  }
  
}

/**
 * A Simple Crypto Class
 * 
 * NOTE: Use at your own risk.  We make no claim that any of this Crypto code is correct.
 */
final class Crypto private (key: Array[Byte], cipher: Crypto.Cipher) extends Logging {
  import Crypto.base64Decode
  
  @deprecated("Use the Crypto object factory methods instead of directly calling this constructor.  e.g. Crypto.defaultCipherForRawKey or Crypto.defaultCipherForBase64Key", "")
  def this(key: Array[Byte]) = this(key, new Crypto.DefaultCipher)
  
  @deprecated("Use the Crypto object factory methods instead of directly calling this constructor.  e.g. Crypto.defaultCipherForRawKey or Crypto.defaultCipherForBase64Key", "")
  def this(base64Key: String) = this(Crypto.base64Decode(base64Key), new Crypto.DefaultCipher)
  
  private[this] val DefaultMac: Mac = new HMac(new SHA1Digest)
  
  private[this] val keyLengthBits: Int = Crypto.DefaultKeyLengthBits
  private[this] val mac: Mac = DefaultMac
  private[this] val secureRandom: SecureRandom = new SecureRandom()
  
  private def ENCRYPT: Boolean = true
  private def DECRYPT: Boolean = false
    
  private[this] val keyLenBytes: Int = {
    require(keyLengthBits % 8 == 0, "keyLengthBits should be a multiple of 8")
    keyLengthBits / 8
  }
  
  private[this] val keyBytes: Array[Byte] = {
    if(key.length == keyLenBytes) key
    else if(key.length > keyLenBytes) {
      logger.warn(s"Key is too long (${key.length * 8} bits).  It is being truncated to $keyLengthBits")
      key.slice(0, keyLenBytes) // truncate
    } else {
      logger.warn(s"Key too short (${key.length * 8} bits).  Using sha256 to expand it")
      require(keyLengthBits == 256, s"Can't expand using sha256 since key is not 256 bits.  Key is $keyLengthBits bits.")
      // NOTE: this needs to be replaced with a proper key derivation function but we first need to figure out if any production code relies on this functionality.
      //       Some tests in MessageCrypto rely on it but I don't think any production code does.
      sha256(key)
    }
    
  }
  
  def encryptBase64String(plaintext: String): String = encryptBase64String(plaintext, urlSafe = false)

  def encryptBase64(bytes: Array[Byte]): String = encryptBase64(bytes, urlSafe = false)
  
  def encryptBase64StringURLSafe(plaintext: String): String = encryptBase64String(plaintext, urlSafe = true)

  def encryptBase64URLSafe(bytes: Array[Byte]): String = encryptBase64(bytes, urlSafe = true)
  
  def encryptBase64String(plaintext: String, urlSafe: Boolean): String = encryptBase64(plaintext.getBytes(UTF_8), urlSafe)

  def encryptBase64(bytes: Array[Byte], urlSafe: Boolean): String = {
    Base64.encodeBytes(encrypt(bytes), if (urlSafe) Base64.URL_SAFE else Base64.NO_OPTIONS)
  }

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
    val ciphertext: Array[Byte] = doCipher(ENCRYPT, iv, plaintext)
    (iv, ciphertext)
  }
  
  /** 
   * Attempt to decrypt a string encrypted using encryptBase64String()
   * 
   * If successful then Some(plaintext) will be returned.  Otherwise None will be returned.
   */
  def tryDecryptBase64String(base64IvAndCiphertext: String): Option[String] = tryWrap{ decryptBase64String(base64IvAndCiphertext) }
  
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
    doCipher(DECRYPT, iv, ciphertext)
  }
  
  /** Decrypt given the IV and Ciphertext */
  def decrypt(iv: Array[Byte], ciphertext: Array[Byte]): Array[Byte] = doCipher(DECRYPT, iv, ciphertext)

  @inline private def tryWrap[T](f: => T): Option[T] = {
    try {
      Some(f)
    } catch {
      case _: IllegalArgumentException => None
      case _: java.io.IOException => None // fm.common.Base64.decode throws an IOException
      case _: org.bouncycastle.crypto.CryptoException => None
      case _: org.bouncycastle.crypto.RuntimeCryptoException => None
    } 
  }
  
  def macBase64(data: String): String = macBase64(data.getBytes(UTF_8))
  def macBase64URLSafe(data: String): String = macBase64URLSafe(data.getBytes(UTF_8))
  def macBase64(data: String, urlSafe: Boolean): String = macBase64(data.getBytes(UTF_8), urlSafe = urlSafe)
  
  /** The Base64 Encoded MAC for an array of bytes */
  def macBase64(data: Array[Byte]): String = macBase64(data, urlSafe = false)
  
  def macBase64URLSafe(data: Array[Byte]): String = macBase64(data, urlSafe = true)
  
  def macBase64(data: Array[Byte], urlSafe: Boolean): String = Base64.encodeBytes(mac(data), if (urlSafe) Base64.URL_SAFE else Base64.NO_OPTIONS)
  
  /** The Hex Encoded MAC for a String */
  def macHex(data: String): String = macHex(data.getBytes(UTF_8))
  
  /** The Hex Encoded MAC for an array of bytes */
  def macHex(data: Array[Byte]): String = new String(Base16.encode(mac(data)))
   
  /** Calculate the MAC for an array of bytes */
  def mac(data: Array[Byte]): Array[Byte] = mac.synchronized {
    mac.init(new KeyParameter(keyBytes))
    mac.update(data, 0, data.length)
    val out: Array[Byte] = new Array[Byte](mac.getMacSize)
    mac.doFinal(out, 0)
    out
  }
  
  private def sha256(data: Array[Byte]): Array[Byte] = {
    // Note: doDigest was manually inlined here since proguard complains and then scala (at least the console) also complains
    val d: Digest = new SHA256Digest
    d.update(data, 0, data.length)
    val buf = new Array[Byte](d.getDigestSize)
    d.doFinal(buf, 0)
    buf
  }

  private def doCipher(doEncrypt: Boolean, iv: Array[Byte], data: Array[Byte]): Array[Byte] = cipher.synchronized{
    cipher.init(doEncrypt, keyBytes, iv)
    val estimatedSize: Int = cipher.getOutputSize(data.length)
    val outBytes: Array[Byte] = new Array[Byte](estimatedSize)
    var outLen: Int = cipher.processBytes(data, 0, data.length, outBytes, 0)
    outLen += cipher.doFinal(outBytes, outLen)
    if (outLen < estimatedSize) {
      val tmp: Array[Byte] = new Array[Byte](outLen)
      System.arraycopy(outBytes, 0, tmp, 0, outLen)
      tmp
    } else outBytes
  }
  
  // Commenting out and inlining manually since proguard complains and the scala console doesn't like this
//  private def doDigest(d: Digest, data: Array[Byte]): Array[Byte] = {
//    d.update(data, 0, data.length)
//    val buf = new Array[Byte](d.getDigestSize)
//    d.doFinal(buf, 0)
//    buf
//  }
}