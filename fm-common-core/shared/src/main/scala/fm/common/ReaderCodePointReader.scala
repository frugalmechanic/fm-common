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

import java.io.Reader
import scala.annotation.tailrec

object ReaderCodePointReader {

  case object GarbageInGarbageOut extends Mode
  case object SkipInvalid extends Mode
  case object Strict extends Mode

  sealed trait Mode
}

final class ReaderCodePointReader(reader: Reader, mode: ReaderCodePointReader.Mode) extends CodePointReader {
  def this(reader: Reader) = this(reader, ReaderCodePointReader.SkipInvalid)

  import ReaderCodePointReader.{GarbageInGarbageOut, SkipInvalid, Strict}

  private var isHeadSet: Boolean = false
  private var hd: Int = 0
  private var readBuffer: Array[Char] = null

  override def close(): Unit = reader.close()

  override def read(buf: Array[Int], off: Int, len: Int): Int = {
    if (off < 0  || off > buf.length || len < 0 || off + len > buf.length || off + len < 0) {
      throw new IndexOutOfBoundsException()
    }

    if (len === 0) {
      return 0
    }

    if (readBuffer === null || readBuffer.length < len) readBuffer = new Array(len)

    var charsRead: Int = if (isHeadSet) {
      // If anything is in hd then we need to use it
      readBuffer(0) = hd.toChar
      isHeadSet = false

      if (len === 1) {
        1 // Nothing to read from the reader since only 1 codepoint was requested
      } else {
        // We've always read at least one character since hd was set so we need
        // to adjust the reader.read result to reflect that.
        val additionalRead: Int = reader.read(readBuffer, 1, len - 1)
        if (additionalRead === -1) 1 else additionalRead + 1
      }
    } else {
      reader.read(readBuffer, 0, len)
    }

    if (charsRead === -1) {
      return -1
    }

    var remaining: Int = len
    var previousHighSurrogate: Char = 0 // 0 is not a validate High Surrogate so we can use it as a marker

    def addToOut(codepoint: Int): Unit = {
      val idx: Int = off + len - remaining

      assert(remaining > 0)
      assert(idx >= off)
      assert(idx < buf.length)

      buf(idx) = codepoint
      remaining -= 1
    }

    while (charsRead >= 0 && remaining > 0) {
      var i: Int = 0
      while (i < charsRead) {
        val ch: Char = readBuffer(i)

        if (previousHighSurrogate =!= 0.toChar) {
          // The previous character was a High Surrogate so we need to see if this character goes with it

          if (Character.isLowSurrogate(ch)) {
            addToOut(Character.toCodePoint(previousHighSurrogate, ch))
            previousHighSurrogate = 0
          } else if (Character.isHighSurrogate(ch)) {
            // We have two High Surrogate character in a row
            mode match {
              case GarbageInGarbageOut =>
                // Write out the unmatched High Surrogate
                addToOut(previousHighSurrogate)

                // Try again with the next High Surrogate
                previousHighSurrogate = ch

              case SkipInvalid =>
                // Skip the previous character and try again with this character
                previousHighSurrogate = ch

              case Strict =>
                throw new IllegalArgumentException(s"Previous Character was a high surrogate but this character is also a high surrogate.  Previous Character: ${previousHighSurrogate.toInt}  This Character:  ${ch.toInt}")
            }

          } else {

            // This is a normal character but the previous was a High Surrogate
            mode match {
              case GarbageInGarbageOut =>
                // Write out the original character
                addToOut(previousHighSurrogate)

                // If we still have space then write out the second character (otherwise save it in hd)
                if (remaining > 1) {
                  addToOut(ch)
                } else {
                  setHd(ch)
                }

              case SkipInvalid =>
                // Ignore the previous high surrogate but write out this valid character
                addToOut(ch)

              case Strict =>
                throw new IllegalArgumentException(s"Previous Character was a high surrogate but this character is not a low (or high) surrogate.  Previous Character: ${previousHighSurrogate.toInt}  This Character:  ${ch.toInt}")
            }

            // This is a normal character so we can also zero out the previousHighSurrogate
            previousHighSurrogate = 0
          }
        } else {
          // Previous was not a high surrogate

          if (Character.isHighSurrogate(ch)) {
            previousHighSurrogate = ch
          } else if (Character.isLowSurrogate(ch)) {
            mode match {
              case GarbageInGarbageOut => addToOut(ch)
              case SkipInvalid => // Skip this character
              case Strict => throw new IllegalArgumentException(s"Found a low surrogate without a high surrogate: ${ch.toInt}")
            }
          } else {
            // Normal character
            addToOut(ch)
          }
        }

        i += 1
      }

      if (remaining > 0) charsRead = reader.read(readBuffer, 0, remaining)
    }

    // If we have anything left in previousHighSurrogate then we should save it to hd for the
    // next call to the read method (or include it if we remaining space and are in GarbageInGarbageOut mode)
    if (previousHighSurrogate =!= 0.toChar) {

      // If we are still expecting remaining data then we must be at the EOF and have an unmatched high surrogate.
      // Note: remaining > 0 means we are at the end of the Reader stream
      if (remaining > 0) {
        mode match {
          case GarbageInGarbageOut => addToOut(previousHighSurrogate)
          case SkipInvalid => // Skip the previousHighSurrogate since we are at the EOF
          case Strict => throw new IllegalArgumentException(s"Found a high surrogate without a low surrogate at the EOF: ${previousHighSurrogate.toInt}")
        }
      } else {
        setHd(previousHighSurrogate)
      }
    }

    len - remaining
  }

  // This method is kept short to allow the JVM to inline it.  The common case
  // will be reading non-supplementary code points (which can be inlined) and
  // the slower case of handling surrogates calls out to a larger method.
  override def read(): Int = {
    val ch: Int = if (isHeadSet) {
      isHeadSet = false
      hd
    } else {
      reader.read()
    }

    if (Character.isSurrogate(ch.toChar)) handlePossibleSurrogate(ch.toChar)
    else ch
  }

  /**
   * This is the slow path for read() or for recursive calls made within this method.
   */
  @tailrec
  private def handlePossibleSurrogate(ch: Int): Int = {
    assert(!isHeadSet, "Expected isHeadSet to be false in the handlePossibleSurrogate method")

    if (Character.isHighSurrogate(ch.toChar)) {
      // Read the second character
      val next: Int = reader.read()

      if (-1 === next) {
        mode match {
          case GarbageInGarbageOut => ch
          case SkipInvalid => -1
          case Strict => throw new IllegalArgumentException(s"Found an un-paired high surrogate character at the EOF: ${ch.toInt}")
        }
      } else if (Character.isHighSurrogate(next.toChar)) {
        mode match {
          case GarbageInGarbageOut => setHd(next); ch
          case SkipInvalid => handlePossibleSurrogate(next) // Skip the current character and try again with the next character
          case Strict => throw new IllegalArgumentException(s"Found an un-paired high surrogate character at the EOF: ${ch.toInt}")
        }
      } else if (Character.isLowSurrogate(next.toChar)){
        Character.toCodePoint(ch.toChar, next.toChar)
      } else {
        mode match {
          case GarbageInGarbageOut => setHd(next); ch
          case SkipInvalid => next
          case Strict => throw new IllegalArgumentException(s"Found an un-paired high surrogate character at the EOF: ${ch.toInt}")
        }
      }

    } else if (Character.isLowSurrogate(ch.toChar)) {
      mode match {
        case GarbageInGarbageOut => ch
        case SkipInvalid => handlePossibleSurrogate(reader.read())
        case Strict => throw new IllegalArgumentException(s"Found an un-paired low surrogate character: ${ch.toInt}")
      }
    } else {
      ch
    }
  }

  override def mark(readAheadLimit: Int): Unit = {
    // If the underlying Reader supports Buffering then we attempt to use that.  We size the
    // buffer double to what is requested to support supplementary characters.
    reader.mark(readAheadLimit * 2)
  }

  override def markSupported(): Boolean = reader.markSupported()

  override def reset(): Unit = reader.reset()

  override def ready(): Boolean = {
    // Not completely accurate since there should be a High Surrogate character reader for reading
    // but not the corresponding Low Surrogate character.  Hopefully this is unlikely.
    reader.ready()
  }

  private def setHd(ch: Int): Unit = {
    isHeadSet = true
    hd = ch
  }
}
