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

import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.{ClosedChannelException, SeekableByteChannel}
import java.nio.file.{Files, StandardOpenOption}
import scala.annotation.tailrec
import scala.util.Try

object MultiReadOnlySeekableByteChannel {
  def forFiles(files: Seq[File]): SeekableByteChannel = {
    forSeekableByteChannels(files.map{ f: File => Files.newByteChannel(f.toPath, StandardOpenOption.READ) })
  }

  def forSeekableByteChannels(channels: Seq[SeekableByteChannel]): SeekableByteChannel = {
    if (channels.isEmpty) throw new IllegalArgumentException("Must have at least 1 SeekableByteChannel")
    else if (channels.size === 1) return channels.head
    else new MultiReadOnlySeekableByteChannel(channels.toArray)
  }
}

/**
 * Takes multiple SeekableByteChannel instances and turns them into a single virtual one.
 *
 * This is used for reading multi-part 7 Zip archives via commons-compress where we just need to concatenate all the
 * parts of the 7 Zip archive into a single SeekableByteChannel which can then be used with the commons-compress
 * SevenZFile class.
 */
final class MultiReadOnlySeekableByteChannel private (channels: Array[SeekableByteChannel]) extends SeekableByteChannel {
  private[this] var currentChannelIdx: Int = 0
  private[this] var globalPosition: Long = 0

  override def read(dst: ByteBuffer): Int = synchronized {
    val bytesRead: Int = read0(dst, 0)
    if (bytesRead > 0) globalPosition += bytesRead
    bytesRead
  }

  @tailrec
  private def read0(dst: ByteBuffer, totalBytesRead: Int): Int = {
    if (!isOpen) throw new ClosedChannelException()

    // EOF - Nothing else to read
    if (currentChannelIdx >= channels.length) {
      return if (totalBytesRead > 0) totalBytesRead else -1
    }

    val currentChannel: SeekableByteChannel = channels(currentChannelIdx)
    val newBytesRead: Int = currentChannel.read(dst)

    if (newBytesRead === -1) {
      // EOF for this channel -- advance to next channel idx
      currentChannelIdx += 1
      read0(dst, totalBytesRead)
    } else if (newBytesRead === 0) {
      // Nothing read so just return whatever was passed in
      totalBytesRead
    } else if (dst.hasRemaining && currentChannel.position() >= currentChannel.size()) {
      // We have more room in the buffer and are at the end of the current channel
      currentChannelIdx += 1
      read0(dst, totalBytesRead + newBytesRead)
    } else {
      totalBytesRead + newBytesRead
    }
  }

  override def position(newPosition: Long): SeekableByteChannel = synchronized {
    if (!isOpen) throw new ClosedChannelException()
    if (newPosition < 0) throw new IllegalArgumentException("Negative position: "+newPosition)

    globalPosition = newPosition

    var i: Int = 0
    var pos: Long = newPosition

    while (i < channels.size) {
      val currentChannel: SeekableByteChannel = channels(i)
      val size: Long = currentChannel.size()

      val newChannelPos: Long = if (pos === -1L) {
        // Position is already set for the correct channel, the rest of the channels get reset to 0
        0
      } else if (pos <= size) {
        // This channel is where we want to be
        currentChannelIdx = i
        val tmp: Long = pos
        pos = -1L // Mark pos as already being set
        tmp
      } else {
        // newPosition is past this channel.  Set channel position to the end and substract channel size from pos
        pos -= size
        size
      }

      currentChannel.position(newChannelPos)
      i += 1
    }

    this
  }

  override def close(): Unit = channels.foreach{ ch: SeekableByteChannel => Try{ ch.close() } }
  override def isOpen: Boolean = channels.forall{ _.isOpen }
  override def position(): Long = globalPosition
  override def size(): Long = channels.map{ _.size() }.sum
  override def truncate(size: Long): SeekableByteChannel = throwReadOnly()
  override def write(src: ByteBuffer): Int = throwReadOnly()

  private def throwReadOnly(): Nothing = throw new NotImplementedError("MultiReadOnlySeekableByteChannel is ReadOnly")
}
