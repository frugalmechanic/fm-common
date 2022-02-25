/*
 * Copyright 2015 Frugal Mechanic (http://frugalmechanic.com)
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

import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object ByteBufferUtil {
  /**
   * Like FileChannel.map except assumes you want the whole file and returns
   * multiple MappedByteBuffers if the file is larger than Integer.MAX_VALUE
   */
  def map(raf: RandomAccessFile, mode: FileChannel.MapMode): Vector[MappedByteBuffer] = map(raf.getChannel(), mode)
  
  /**
   * Like FileChannel.map except assumes you want the whole file and returns
   * multiple MappedByteBuffers if the file is larger than Integer.MAX_VALUE
   */
  def map(ch: FileChannel, mode: FileChannel.MapMode): Vector[MappedByteBuffer] = {
    val totalSize: Long = ch.size()
    
    if (totalSize == 0) return Vector.empty
    if (totalSize <= Int.MaxValue) return Vector(ch.map(mode, 0, totalSize))
    
    val builder = Vector.newBuilder[MappedByteBuffer]
    
    var start: Long = 0
    var size: Long = totalSize
    
    while(size > 0) {
      val thisSize: Long = math.min(Int.MaxValue.toLong, size)
      builder += ch.map(mode, start, thisSize)
      start += thisSize
      size -= thisSize
    }
    
    builder.result()
  }
}