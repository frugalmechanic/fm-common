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
import java.nio.{ByteBuffer, MappedByteBuffer}

/**
 * A Simple OutputStream wrapper around a ByteBuffer
 */
final class ByteBufferOutputStream(buf: ByteBuffer) extends OutputStream {
  def write(b: Int): Unit = buf.put(b.toByte)
  
  override def write(bytes: Array[Byte], off: Int, len: Int): Unit = buf.put(bytes, off, len)
  
  /**
   * If this is a MappedByteBuffer then force() is called to cause changes to be written to disk
   */
  override def flush(): Unit = buf match {
    case mapped: MappedByteBuffer => mapped.force()
    case _ => // Do nothing
  }
}
