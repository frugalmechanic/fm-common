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
import java.util.Comparator

/**
 * This is here so that LazySeq can be split out into it's own project.
 * 
 * Some implementations are still in our internal Util package for now
 * until we get a chance to refactor and possible merge with our fm-serializer
 * project.
 */
trait Serializer[T] {
  def serialize(value: T): Array[Byte]
  def deserialize(bytes: Array[Byte]): T
}

/**
 * Implicits that go along with the Serializer trait
 */
object Serializer {
  implicit object StringSerializer extends Serializer[String] {
    final def serialize(value: String) = value.getBytes(UTF_8)
    final def deserialize(bytes: Array[Byte]) = new String(bytes, UTF_8)
  }
  
  implicit object ByteArraySerializer extends Serializer[Array[Byte]] {
    final def serialize(value: Array[Byte]) = value
    final def deserialize(bytes: Array[Byte]) = bytes
  }
  
  implicit object BooleanSerializer extends Serializer[Boolean] {
    final def serialize(value: Boolean) = Array[Byte](if(value) 1 else 0)
    final def deserialize(bytes: Array[Byte]) = if(bytes(0) == 0) false else true
  }
  
  // TODO: Possibly remove this after figuring out if internal stuff will break if it's not the default
  implicit object TruncatedIntSerializer extends Serializer[Int] with Comparator[Array[Byte]] {
    final def serialize(value: Int) = truncatedBytes(value)
    final def deserialize(bytes: Array[Byte]) = truncatedInt(bytes)
  
    final def compare(l: Array[Byte], r:Array[Byte]): Int = {
      val li = deserialize(l)
      val ri = deserialize(r)
  
      if(li < ri) -1
      else if (li > ri) 1
      else 0
    }
  }

  // TODO: Possibly remove this after figuring out if internal stuff will break if it's not the default
  implicit object TruncatedLongSerializer extends Serializer[Long] with Comparator[Array[Byte]] {
    final def serialize(value: Long) = truncatedBytes(value)
    final def deserialize(bytes: Array[Byte]) = truncatedLong(bytes)
  
    final def compare(l: Array[Byte], r: Array[Byte]): Int = {
      val li = deserialize(l)
      val ri = deserialize(r)
  
      if(li < ri) -1
      else if (li > ri) 1
      else 0
    }
  }
  
  /**
   * Convert an Int to a truncated byte array depending on how many
   * bytes are actually needed to store the value. Any leading bytes that are
   * zero are truncated.
   *
   * Examples:
   *  1 => 0x01
   *  255 => 0xff
   *  1024 => 0x0400
   */
  private def truncatedBytes(value: Int): Array[Byte] = {
    var len = 4
    var mask = 0xff000000

    // Figure out how many bytes we need to store the int
    while((value & mask) == 0 && len > 1) {
      len -= 1
      mask = mask >>> 8
    }

    val bytes = new Array[Byte](len)

    var v = value
    var i = len - 1

    while(i >= 0) {
      bytes(i) = (v & 0xff).byteValue
      v = v >>> 8
      i -= 1
    }

    bytes
  }

  private def truncatedInt(bytes: Array[Byte]): Int = {
    val len = bytes.length

    var idx = 0
    var value = 0

    while(idx < len) {
      value = (value << 8) | (0xff & bytes(idx))
      idx += 1
    }

    value
  }

  private def truncatedBytes(value: Long): Array[Byte] = {
    var len: Int = 8
    var mask: Long = 0xff00000000000000L

    // Figure out how many bytes we need to store the int
    while((value & mask) == 0 && len > 1) {
      len -= 1
      mask = mask >>> 8
    }

    val bytes: Array[Byte] = new Array[Byte](len)

    var v: Long = value
    var i: Int = len - 1

    while(i >= 0) {
      bytes(i) = (v & 0xff).byteValue
      v = v >>> 8
      i -= 1
    }

    bytes
  }

  private def truncatedLong(bytes: Array[Byte]): Long = {
    val len: Int = bytes.length

    var idx: Int = 0
    var value: Long = 0L

    while(idx < len) {
      value = (value << 8) | (0xff & bytes(idx))
      idx += 1
    }

    value
  }
}