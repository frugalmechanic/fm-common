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

import java.io.{FilterInputStream, FilterOutputStream}
import java.nio.{ByteBuffer, CharBuffer}
import java.nio.charset.StandardCharsets

/**
 * Base64 encoding/decoding methods.
 *
 * Note: This will decode normal Base64 and the modified Base64 for URL variant.  If you don't
 *       want this behavior then use Base64Strict or Base64URL directly.
 */
object Base64 extends BaseEncoding {
  final class InputStream(is: java.io.InputStream) extends FilterInputStream(java.util.Base64.getDecoder().wrap(new URLToStrictInputStream(is)))
  final class OutputStream(os: java.io.OutputStream) extends FilterOutputStream(java.util.Base64.getEncoder().wrap(os))

  /** This translates from the Base64 URL Variant to Normal Base64 */
  private[common] class URLToStrictInputStream(is: java.io.InputStream) extends FilterInputStream(is) {
    override def read(): Int = {
      val ch: Int = super.read()

      if (ch == '-') '+'
      else if (ch == '_') '/'
      else ch
    }

    override def read(b: Array[Byte]): Int = fixup(b, 0, super.read(b))
    override def read(b: Array[Byte], off: Int, len: Int): Int = fixup(b, off, super.read(b, off, len))

    private def fixup(b: Array[Byte], off: Int, len: Int): Int = {
      if (-1 == len) return -1

      var i: Int = off

      while (i < off + len) {
        val ch: Int = b(i)
        if (ch == '-') b(i) = '+'
        else if (ch == '_') b(i) = '/'

        i += 1
      }

      len
    }
  }

  override def encode(bytes: Array[Byte]): String = Base64Strict.encode(bytes)
  override def encode(bytes: Array[Byte], offset: Int, length: Int): String = Base64Strict.encode(bytes, offset, length)

  def encodeNoPadding(bytes: Array[Byte]): String = Base64Strict.encodeNoPadding(bytes)
  def encodeNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = Base64Strict.encodeNoPadding(bytes, offset, length)

  def encodeURL(bytes: Array[Byte]): String = Base64URL.encode(bytes)
  def encodeURL(bytes: Array[Byte], offset: Int, length: Int): String = Base64URL.encodeNoPadding(bytes, offset, length)

  def encodeURLNoPadding(bytes: Array[Byte]): String = Base64URL.encodeNoPadding(bytes)
  def encodeURLNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = Base64URL.encodeNoPadding(bytes, offset, length)

  def decode(data: ByteBuffer): ByteBuffer = {
    var i: Int = 0
    var isStrict: Boolean = false
    var isURL: Boolean = false

    val offset: Int = data.position()
    val length: Int = data.limit() - data.position()

    while (i < length && !(isStrict || isURL)) {
      val ch: Char = data.get(offset + i).toChar
      if (ch == '+' || ch == '/') isStrict = true
      else if (ch == '-' || ch == '_') isURL = true
      i += 1
    }

    if (isURL) Base64URL.decode(data) else Base64Strict.decode(data)
  }

  def decode(data: Array[Byte]): Array[Byte] = {
    var i: Int = 0
    var isStrict: Boolean = false
    var isURL: Boolean = false

    while (i < data.length && !(isStrict || isURL)) {
      val ch: Char = data(i).toChar
      if (ch == '+' || ch == '/') isStrict = true
      else if (ch == '-' || ch == '_') isURL = true
      i += 1
    }

    if (isURL) Base64URL.decode(data) else Base64Strict.decode(data)
  }

  def decode(data: Array[Char]): Array[Byte] = {
    var i: Int = 0
    var isStrict: Boolean = false
    var isURL: Boolean = false

    while (i < data.length && !(isStrict || isURL)) {
      val ch: Char = data(i)
      if (ch == '+' || ch == '/') isStrict = true
      else if (ch == '-' || ch == '_') isURL = true
      i += 1
    }

    if (isURL) Base64URL.decode(data) else Base64Strict.decode(data)
  }

  def decode(data: CharSequence): Array[Byte] = {
    var i: Int = 0
    var isStrict: Boolean = false
    var isURL: Boolean = false

    while (i < data.length && !(isStrict || isURL)) {
      val ch: Char = data.charAt(i)
      if (ch == '+' || ch == '/') isStrict = true
      else if (ch == '-' || ch == '_') isURL = true
      i += 1
    }

    if (isURL) Base64URL.decode(data) else Base64Strict.decode(data)
  }
}

object Base64Strict extends Base64Impl(java.util.Base64.getEncoder(), java.util.Base64.getDecoder())
object Base64URL extends Base64Impl(java.util.Base64.getUrlEncoder(), java.util.Base64.getUrlDecoder())

abstract class Base64Impl(encoder: java.util.Base64.Encoder, decoder: java.util.Base64.Decoder) extends BaseEncoding {
  private val encoderNoPadding: java.util.Base64.Encoder = encoder.withoutPadding()

  override def decode(data: Array[Byte]): Array[Byte] = decoder.decode(data)
  override def decode(data: ByteBuffer): ByteBuffer = decoder.decode(data)
  override def decode(data: Array[Char]): Array[Byte] = decode(CharBuffer.wrap(data))
  override def decode(data: CharSequence): Array[Byte] = decoder.decode(data.toString())

  override def encode(bytes: Array[Byte]): String = encoder.encodeToString(bytes)

  override def encode(bytes: Array[Byte], offset: Int, length: Int): String = {
    new String(encoder.encode(ByteBuffer.wrap(bytes, offset, length)).array(), StandardCharsets.UTF_8)
  }

  def encodeNoPadding(bytes: Array[Byte]): String = encoderNoPadding.encodeToString(bytes)

  def encodeNoPadding(bytes: Array[Byte], offset: Int, length: Int): String = {
    new String(encoderNoPadding.encode(ByteBuffer.wrap(bytes, offset, length)).array(), StandardCharsets.UTF_8)
  }

  final class InputStream(is: java.io.InputStream) extends FilterInputStream(decoder.wrap(is))
  final class OutputStream(os: java.io.OutputStream) extends FilterOutputStream(encoder.wrap(os))
}