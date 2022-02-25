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

import java.io.{FilterReader, Reader}

abstract class InvalidCharFilterReader(r: Reader, logInvalidChars: Boolean = false) extends FilterReader(r) with Logging {
  // To be implemented by child classes
  def isValidChar(ch: Char): Boolean
  
  // All other methods in FilterReader go through this method
  override def read(buf: Array[Char], off: Int, len: Int): Int = {
    val read: Int = super.read(buf, off, len)
    if (read == -1) return -1
    
    var idx: Int = off        // The idx we are reading from
    var shift: Int = 0        // The amount of characters to shift by
    val end: Int = off + read // Where to stop reading
    
    while (idx < end) {
      val ch: Char = buf(idx)
      
      if (isValidChar(ch)) {
        // Perform the shift (if needed)
        if (shift > 0) buf(idx - shift) = ch
      } else {
        if(logInvalidChars) logger.warn(s"Filtering out character: 0x${"%02X".format(ch.toByte)}")
        // This is an invalid char which means we have to shift everything
        shift += 1
      }

      // This might be overwritten later but null it out for now
      if (shift > 0) buf(idx) = '\u0000'

      idx += 1
    }
    
    read - shift
  }
}