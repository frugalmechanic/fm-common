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
import scala.util.control.Breaks._

object MessageCrypto {
  def apply(key: String, json: Boolean = false) = new MessageCrypto(key,json)
}

/**
 * Compatible with the Rails MessageEncryptor using 'aes-256-cbc' and
 * MessageVerifier using 'sha1' when working with *String* values.
 *
 * Also compatible with the custom MessageCrypto which marshalls values as JSON
 * instead of using the ruby Marshal.dump and Marshal.load.  Use json=true
 * to dump using JSON.
 *
 * MessageEncryptor uses Marshal.dump and Marshal.load on whatever values you
 * are trying to encrypt/sign.  A subset of Marshal.dump and Marshal.load have
 * been implemented to support String values.
 * 
 * NOTE: This is a legacy class that was created when we switched from
 *       Rails to Scala.  It is still used in a few places but probably
 *       needs to be refactored to remove the old Ruby marshalling stuff.
 */
final class MessageCrypto(key: Array[Byte], json: Boolean = false) {
  def this(key: String) = this(key.getBytes(UTF_8))
  def this(key: String, json: Boolean) = this(key.getBytes(UTF_8), json)
  
  private[this] val crypto: Crypto = Crypto.defaultCipherForRawKey(key)

  def encryptAndSign(msg: String): String = sign(encrypt(msg))
  def decryptAndVerify(msg: String): Option[String] = verify(msg).map{decrypt}

  def encrypt(msg: String): String = {
    val (iv: Array[Byte], ciphertext: Array[Byte]) = crypto.encryptRaw(dump(msg))
    Array(ciphertext,iv).map{s => Base64.encodeBytes(s)}.mkString("--")
  }

  def decrypt(msg: String): String = {
    val lastIdx: Int = msg.lastIndexOf("--")

    if (-1 === lastIdx) throw new IllegalArgumentException("Message is in invalid format")

    val ciphertext: String = msg.substring(0, lastIdx)
    val iv: String = msg.substring(lastIdx+2)

    val plaintext = crypto.decrypt(Base64.decode(iv), Base64.decode(ciphertext))
    load(plaintext)
  }

  def sign(msg: String): String = {
    val data: String = Base64.encodeBytes(dump(msg))
    data+"--"+hexHmac(data)
  }

  def verify(msg: String): Option[String] = {
    val lastIdx: Int = msg.lastIndexOf("--")

    if (-1 === lastIdx) return None

    val data: String = msg.substring(0, lastIdx)
    val sig: String = msg.substring(lastIdx+2)

    if (sig != hexHmac(data)) {
      None
    } else {
      val bytes = Base64.decode(data.getBytes(UTF_8))
      Some(load(bytes))
    }
  }

  def hexHmac(msg: String): String = crypto.macHex(msg)

  private def dump(s: String): Array[Byte] = if (json) jsonDump(s) else rubyMarshalDump(s)
  
  private def load(b: Array[Byte]): String = {
    // If the byte array starts and ends with { and } then it's a JSON hash (currently unsupported)
    if (b(0) === '{'.toByte && b(b.length-1) === '}'.toByte) return ""

    // If the byte array starts and ends with quotes then it's json otherwise use the ruby unmarshal
    if (b(0) === '"'.toByte && b(b.length-1) === '"'.toByte) jsonLoad(b) else rubyMarshalLoad(b)
  }

  private def rubyMarshalDump(s: String): Array[Byte] = {
    val marshal = new RubyMarshalStream
    marshal.writeString(s)
    marshal.out.toByteArray
  }
  
  private def rubyMarshalLoad(b: Array[Byte]):String = {
    val unmarshal: RubyUnmarshalStream = new RubyUnmarshalStream(b)
    unmarshal.readString
  }

  private def jsonDump(s: String): Array[Byte] = ("\""+s+"\"").getBytes(UTF_8)
  private def jsonLoad(b: Array[Byte]): String = new String(b, 1, b.length-2, UTF_8)

  // http://github.com/jruby/jruby/blob/master/src/org/jruby/runtime/marshal/MarshalStream.java
  private class RubyMarshalStream {
    private[this] val MARSHAL_MAJOR: Int = 4
    private[this] val MARSHAL_MINOR: Int = 8

    val out = new java.io.ByteArrayOutputStream
    out.write(MARSHAL_MAJOR)
    out.write(MARSHAL_MINOR)

    def writeString(s: String): Unit = {
      out.write('"'.toInt)
      writeStringBytes(s.getBytes(UTF_8))
    }

    def writeStringBytes(b: Array[Byte]): Unit = {
      writeInt(b.length)
      out.write(b)
    }

    def writeInt(v: Int): Unit = {
      var value: Int = v

      if (value === 0) {
        out.write(0)
      } else if (0 < value && value < 123) {
        out.write(value + 5)
      } else if (-124 < value && value < 0) {
        out.write((value - 5) & 0xff)
      } else {
        val buf = new Array[Byte](4)
        var i = 0
        breakable{
          while(i < buf.length) {
            buf(i) = (value & 0xff).toByte

            value = value >> 8
            if (value === 0 || value === -1) {
              break
            }
            i += 1
          }
        }

        val len: Int = i + 1
        out.write(if (value < 0) -len else len)
        out.write(buf, 0, i + 1)
      }
    }
  }

  final class RubyUnmarshalStream(bytes: Array[Byte]) {
    val in = new java.io.ByteArrayInputStream(bytes)
    in.read() // Major
    in.read() // Minor

    def readString(): String = {
      val ch: Char = in.read().toChar
      assert(ch === '"', "Expecting to read a quote. ByteString: "+new String(bytes, UTF_8)+"  Bytes: "+bytes.toSeq)
      val len: Int = readInt()
      val buf = new Array[Byte](len)
      IOUtils.read(in, buf)
      new String(buf, UTF_8)
    }

    def readInt(): Int = {
      var c: Int = readSignedByte().toInt
      if (c === 0) return 0
      else if (5 < c && c < 128) return c - 5
      else if (-129 < c && c < -5) return c + 5

      var result: Long = 0L
      if (c > 0) {
        var i = 0
        while(i < c) {
          result |= readUnsignedByte.toLong << (8 * i)
          i += 1
        }
      } else {
        c = -c
        result = -1
        var i = 0
        while(i < c) {
          result &= ~(0xff.toLong << (8 * i))
          result |= readUnsignedByte.toLong << (8 * i)
          i += 1
        }

      }

      result.toInt
    }

    def readSignedByte(): Byte = {
      val b: Int = readUnsignedByte()
      if (b > 127) (b - 256).toByte else b.toByte
    }

    def readUnsignedByte(): Int = in.read()
  }
}
