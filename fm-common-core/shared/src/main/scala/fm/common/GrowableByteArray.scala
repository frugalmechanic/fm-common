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

import java.io.OutputStream
import java.util.Arrays

/**
 * Like java.io.ByteArrayOutputStream but exposes the internal Array
 */
final class GrowableByteArray(initialSize: Int) extends OutputStream {
  def this() = this(32)
  
  require(initialSize >= 0, "Invalid initialSize: "+initialSize)
  
  private[this] var buf: Array[Byte] = new Array(initialSize)
  private[this] var count: Int = 0
  
  /**
   * The raw array that is being written to
   */
  def array: Array[Byte] = buf
  
  /**
   * How many bytes have been written to the array
   */
  def size: Int = count
  
  def write(b: Int): Unit = {
    ensureCapacity(count + 1)
    buf(count) = b.toByte
    count += 1
  }
  
  override def write(b: Array[Byte], off: Int, len: Int): Unit = {
    if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) - b.length > 0)) throw new IndexOutOfBoundsException(s"write($b, $off, $len)  b.length: ${b.length}")
    ensureCapacity(count + len)
    System.arraycopy(b, off, buf, count, len)
    count += len
  }
  
  def reset(): Unit = count = 0
  
  def toByteArray: Array[Byte] = Arrays.copyOf(buf, count)
  
  def ensureCapacity(minCapacity: Int): Unit = {
    require(minCapacity > 0, "Invalid minCapacity: "+minCapacity)
    if (minCapacity > buf.length) grow(minCapacity)
    assert(buf.length >= minCapacity)
  }
  
  private def grow(minCapacity: Int): Unit = {    
    val curSize: Int = buf.length
    var newSize: Int = curSize << 1
    
    if (newSize < minCapacity) newSize = minCapacity
    
    val old: Array[Byte] = buf
    buf = new Array[Byte](newSize)
    
    System.arraycopy(old, 0, buf, 0, count)
  }
  
}