/*
 * Copyright 2021 Tim Underwood (https://github.com/tpunder)
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

import java.nio.{ByteBuffer, CharBuffer}

object Base16 extends Base16(Array('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f')) {
  def encodeLower(bytes: Array[Byte]): String = Base16Lower.encode(bytes)
  def encodeLower(bytes: Array[Byte], offset: Int, length: Int): String = Base16Lower.encode(bytes, offset, length)

  def encodeUpper(bytes: Array[Byte]): String = Base16Upper.encode(bytes)
  def encodeUpper(bytes: Array[Byte], offset: Int, length: Int): String = Base16Upper.encode(bytes, offset, length)
}

object Base16Lower extends Base16(Array('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'))
object Base16Upper extends Base16(Array('0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'))

abstract class Base16(encodeTable: Array[Byte]) extends BaseEncoding {
  override def encode(bytes: Array[Byte]): String = encode(bytes, 0, bytes.length)
  override def encode(bytes: Array[Byte], offset: Int, length: Int): String = new String(encodeToBytes(bytes, offset, length))

  private def encodeToBytes(in: Array[Byte], offset: Int, length: Int): Array[Byte] = {
    if (null == in || length == 0) return Array()

    val out: Array[Byte] = new Array(length * 2)

    var i: Int = 0

    while (i < length) {
      val b: Byte = in(offset + i)
      val upper: Int = (b & 0xF0) >>> 4
      val lower: Int = b & 0xF

      out(i * 2) = encodeTable(upper)
      out(i * 2 + 1) = encodeTable(lower)

      i += 1
    }

    out
  }

  override def decode(in: Array[Byte]): Array[Byte] = {
    decode(ByteBuffer.wrap(in)).array()
  }

  override def decode(in: ByteBuffer): ByteBuffer = {
    val inLength: Int = in.remaining()
    if (inLength % 2 != 0) throw new IllegalArgumentException(s"Invalid Base16 data length ($inLength). Should be a multiple of 2.")

    val offset: Int = in.position()
    val outLength: Int = inLength / 2
    val out: Array[Byte] = new Array(outLength)

    var i: Int = 0

    while (i < outLength) {
      val upper: Int = decodeByte(in.get(offset + i * 2))
      val lower: Int = decodeByte(in.get(offset + i * 2 + 1))
      out(i) = ((upper << 4) | lower).toByte
      i += 1
    }

    ByteBuffer.wrap(out)
  }

  override def decode(in: Array[Char]): Array[Byte] = {
    decode(CharBuffer.wrap(in))
  }

  override def decode(in: CharSequence): Array[Byte] = {
    val inLength: Int = in.length()
    if (inLength % 2 != 0) throw new IllegalArgumentException(s"Invalid Base16 data length ($inLength). Should be a multiple of 2.")

    val outLength: Int = inLength / 2
    val out: Array[Byte] = new Array(outLength)

    var i: Int = 0

    while (i < outLength) {
      val upper: Int = decodeChar(in.charAt(i * 2))
      val lower: Int = decodeChar(in.charAt(i * 2 + 1))
      out(i) = ((upper << 4) | lower).toByte
      i += 1
    }

    out
  }

  private def decodeByte(ch: Byte): Int = {
    val digit: Int = Character.digit(ch, 16)
    if (-1 == digit) throw new IllegalArgumentException(s"Invalid Base16/Hex character: $ch")
    digit
  }

  private def decodeChar(ch: Char): Int = {
    val digit: Int = Character.digit(ch, 16)
    if (-1 == digit) throw new IllegalArgumentException(s"Invalid Base16/Hex character: $ch")
    digit
  }
}