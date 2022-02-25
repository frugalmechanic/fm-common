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

import java.io.{File, FileInputStream, InputStream}
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
  * MD5/SHA1/SHA256 Helpers
  */
object DigestUtils {
  private val BUFFER_SIZE: Int = 1024

  def md5(data: Array[Byte]): Array[Byte] = digestBytes(makeMD5Digest(), data)
  def md5(data: InputStream): Array[Byte] = digestBytes(makeMD5Digest(), data)
  def md5(data: String)     : Array[Byte] = digestBytes(makeMD5Digest(), data)
  def md5(file: File)       : Array[Byte] = Resource.using(new FileInputStream(file)){ md5 }
  
  def md5Hex(data: Array[Byte]): String = digestHex(makeMD5Digest(), data)
  def md5Hex(data: InputStream): String = digestHex(makeMD5Digest(), data)
  def md5Hex(data: String)     : String = digestHex(makeMD5Digest(), data)
  def md5Hex(file: File)       : String = Resource.using(new FileInputStream(file)){ md5Hex }
  
  def sha1(data: Array[Byte]): Array[Byte] = digestBytes(makeSHA1Digest(), data)
  def sha1(data: InputStream): Array[Byte] = digestBytes(makeSHA1Digest(), data)
  def sha1(data: String)     : Array[Byte] = digestBytes(makeSHA1Digest(), data)
  def sha1(file: File)       : Array[Byte] = Resource.using(new FileInputStream(file)){ sha1 }
  
  def sha1Hex(data: Array[Byte]): String = digestHex(makeSHA1Digest(), data)
  def sha1Hex(data: InputStream): String = digestHex(makeSHA1Digest(), data)
  def sha1Hex(data: String)     : String = digestHex(makeSHA1Digest(), data)
  def sha1Hex(file: File)       : String = Resource.using(new FileInputStream(file)){ sha1Hex }
  
  def sha256(data: Array[Byte]): Array[Byte] = digestBytes(makeSHA256Digest(), data)
  def sha256(data: InputStream): Array[Byte] = digestBytes(makeSHA256Digest(), data)
  def sha256(data: String)     : Array[Byte] = digestBytes(makeSHA256Digest(), data)
  def sha256(file: File)       : Array[Byte] = Resource.using(new FileInputStream(file)){ sha256 }
  
  def sha256Hex(data: Array[Byte]): String = digestHex(makeSHA256Digest(), data)
  def sha256Hex(data: InputStream): String = digestHex(makeSHA256Digest(), data)
  def sha256Hex(data: String)     : String = digestHex(makeSHA256Digest(), data)
  def sha256Hex(file: File)       : String = Resource.using(new FileInputStream(file)){ sha256Hex }

  private def makeMD5Digest(): MessageDigest = MessageDigest.getInstance("MD5")
  private def makeSHA1Digest(): MessageDigest = MessageDigest.getInstance("SHA-1")
  private def makeSHA256Digest(): MessageDigest = MessageDigest.getInstance("SHA-256")

  private def digestHex(digest: MessageDigest, data: String): String = {
    digestHex(digest, data.getBytes(StandardCharsets.UTF_8))
  }

  private def digestHex(digest: MessageDigest, data: Array[Byte]): String = {
    Base16.encode(digestBytes(digest, data))
  }

  private def digestHex(digest: MessageDigest, in: InputStream): String = {
    Base16.encode(digestBytes(digest, in))
  }

  private def digestBytes(digest: MessageDigest, data: String): Array[Byte] = {
    digest.digest(data.getBytes(StandardCharsets.UTF_8))
  }

  private def digestBytes(digest: MessageDigest, data: Array[Byte]): Array[Byte] = {
    digest.digest(data)
  }

  private def digestBytes(digest: MessageDigest, data: InputStream): Array[Byte] = {
    val buf: Array[Byte] = new Array(BUFFER_SIZE)

    var bytesRead: Int = data.read(buf)

    while (bytesRead > -1) {
      digest.update(buf, 0, bytesRead)
      bytesRead = data.read(buf)
    }

    digest.digest()
  }
}