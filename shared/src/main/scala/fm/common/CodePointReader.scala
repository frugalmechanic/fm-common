/*
 * Copyright 2019 Frugal Mechanic (http://frugalmechanic.com)
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

import java.io.{IOException, Reader}
import java.nio.IntBuffer

object CodePointReader {
  private val maxSkipBufferSize: Int = 8192

  def apply(reader: Reader): CodePointReader = new ReaderCodePointReader(reader)

  /** Simplified implementation of an empty CodePointReader */
  object empty extends CodePointReader {
    override def close(): Unit = {}
    override def read(buf: Array[Int], off: Int, len: Int): Int = -1
  }
}

/**
 * Similar to Reader except for reading Unicode Code Points instead of Java Characters.
 *
 * See java.io.Reader for API descriptions.  The behavior of this trait should mimic java.io.Reader.
 */
trait CodePointReader {
  private var skipBuffer: Array[Int] = null

  /** Closes the stream and releases any system resources associated with it. */
  def close(): Unit

  /** Reads code points into a portion of an array. */
  def read(buf: Array[Int], off: Int, len: Int): Int

  /** Marks the present position in the stream. */
  def mark(readAheadLimit: Int): Unit = throw new IOException("mark() not supported")

  /** Tells whether this stream supports the mark() operation. */
  def markSupported(): Boolean = false

  /** Resets the stream. */
  def reset(): Unit = throw new IOException("reset() not supported")

  /** Reads a single code point. */
  def read(): Int = {
    val buf: Array[Int] = new Array(1)
    if (read(buf) === -1) -1 else buf(0)
  }

  /** Reads code points into an array. */
  def read(buf: Array[Int]): Int = read(buf, 0, buf.length)

  /** Attempts to read characters into the specified int buffer. */
  def read(target: IntBuffer): Int = {
    val len: Int = target.remaining()
    val buf: Array[Int] = new Array(len)
    val numRead: Int = read(buf, 0, len)
    if (numRead > 0) target.put(buf, 0, numRead)
    numRead
  }

  /** Tells whether this stream is ready to be read. */
  def ready(): Boolean = false

  /** Skips characters. */
  def skip(n: Long): Long = {
    val size: Int = math.min(n, CodePointReader.maxSkipBufferSize).toInt
    if (skipBuffer === null || skipBuffer.length < size) skipBuffer = new Array(size)

    var eof: Boolean = false
    var remaining: Long = n

    while (remaining > 0 && !eof) {
      val numRead: Int = read(skipBuffer, 0, math.min(remaining, size).toInt)
      if (numRead === -1) eof = true
      else remaining -= numRead
    }

    n - remaining
  }

//  /** Reads all characters from this reader and writes the characters to the given writer in the order that they are read. */
//  def transferTo(out: CodePointWriter): Long
}
