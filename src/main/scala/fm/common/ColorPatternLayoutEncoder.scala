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

import ch.qos.logback.classic.Level.{ERROR,WARN}
import ch.qos.logback.classic.spi.ILoggingEvent
import java.io.IOException
import java.nio.charset.StandardCharsets.UTF_8

object ColorPatternLayoutEncoder {
  private val lockObject: Object = new Object{}
}

final class ColorPatternLayoutEncoder extends ch.qos.logback.classic.encoder.PatternLayoutEncoder {
  import ColorPatternLayoutEncoder.lockObject
  
  private[this] val END_COLOR:     Array[Byte] = "\u001b[m".getBytes(UTF_8)
  private[this] val ERROR_COLOR:   Array[Byte] = "\u001b[0;31m".getBytes(UTF_8)
  private[this] val WARN_COLOR:    Array[Byte] = "\u001b[0;33m".getBytes(UTF_8)
  private[this] val DEFAULT_COLOR: Array[Byte] = "".getBytes(UTF_8)

  override def doEncode(e: ILoggingEvent): Unit = lockObject.synchronized {
    val startColor: Array[Byte] = e.getLevel match {
      case ERROR => ERROR_COLOR
      case WARN  => WARN_COLOR
      case _     => DEFAULT_COLOR
    }

    try {
      if (startColor.length != 0) outputStream.write(startColor)
      super.doEncode(e)
    } finally {
      if (startColor.length != 0) try{ outputStream.write(END_COLOR) } catch { case ex: IOException => }
    }
  }
}
