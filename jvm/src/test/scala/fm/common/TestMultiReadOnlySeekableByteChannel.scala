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

import java.nio.ByteBuffer
import java.nio.channels.{ClosedChannelException, SeekableByteChannel}
import java.nio.charset.StandardCharsets
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.scalatest.{FunSuite, Matchers}

final class TestMultiReadOnlySeekableByteChannel extends FunSuite with Matchers {

  test("Empty") {
    an [IllegalArgumentException] shouldBe thrownBy { MultiReadOnlySeekableByteChannel.forSeekableByteChannels(Nil) }
  }

  test("Single - Identity") {
    val channel: SeekableByteChannel = makeEmpty()
    MultiReadOnlySeekableByteChannel.forSeekableByteChannels(Seq(channel)) shouldBe theSameInstanceAs (channel)
  }

  test("Empty InMemorySeekableByteChannel - Reference Behavior") {
    checkEmpty(makeEmpty())
  }

  test("Empty SeekableByteChannel") {
    val channel: SeekableByteChannel = MultiReadOnlySeekableByteChannel.forSeekableByteChannels(Seq(makeEmpty(), makeEmpty()))
    checkEmpty(channel)
  }

  test("Checks - Empty") {
    check(Array())
  }

  test("Checks - Single") {
    check(Array(0x0))
  }

  test("Checks - String") {
    check("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".getBytes(StandardCharsets.UTF_8))
  }

  private def checkEmpty(channel: SeekableByteChannel): Unit = {
    val buf: ByteBuffer = ByteBuffer.allocate(10)

    channel.isOpen shouldBe true
    channel.size() shouldBe 0
    channel.position() shouldBe 0
    channel.read(buf) shouldBe -1

    channel.position(5)
    channel.read(buf) shouldBe -1

    channel.close()
    channel.isOpen() shouldBe false

    an [ClosedChannelException] shouldBe thrownBy { channel.read(buf) }
    an [ClosedChannelException] shouldBe thrownBy { channel.position(100) }
  }

  private def check(expected: Array[Byte]): Unit = {
    (1 to expected.length).foreach { channelSize: Int =>
      // Sanity check that all operations work for SeekableInMemoryByteChannel
      check(expected, makeSingle(expected))

      // Checks against our MultiReadOnlySeekableByteChannel instance
      check(expected, makeMulti(expected.grouped(channelSize)))
    }
  }

  private def check(expected: Array[Byte], channel: SeekableByteChannel): Unit = {
    (1 to expected.length + 5).foreach{ readBufferSize: Int =>
      check(expected, channel, readBufferSize)
    }
  }

  private def check(expected: Array[Byte], channel: SeekableByteChannel, readBufferSize: Int): Unit = {
    channel.isOpen shouldBe true
    channel.size() shouldBe expected.length
    channel.position(0)
    channel.position() shouldBe 0

    // Will hold the entire result that we read
    val resultBuffer: ByteBuffer = ByteBuffer.allocate(expected.length + 100)

    // Used for each read() method call
    val buf: ByteBuffer = ByteBuffer.allocate(readBufferSize)

    var bytesRead: Int = channel.read(buf)

    while (bytesRead ≠ -1) {
      val remaining: Int = buf.remaining()

      buf.flip()
      resultBuffer.put(buf)
      buf.clear()
      bytesRead = channel.read(buf)

      // If this isn't the last read() then we expect the buf ByteBuffer to be full (i.e. have no remaining)
      if (resultBuffer.position() < expected.length) remaining shouldBe 0

      if (bytesRead ≡ -1) buf.position() shouldBe 0
      else buf.position() shouldBe bytesRead
    }

    resultBuffer.flip()
    val arr: Array[Byte] = new Array(resultBuffer.remaining())
    resultBuffer.get(arr)

    arr.toIndexedSeq shouldBe expected.toIndexedSeq
  }

  private def makeEmpty(): SeekableByteChannel = {
    makeSingle(new Array[Byte](0))
  }

  private def makeSingle(arr: Array[Byte]): SeekableByteChannel = {
    new SeekableInMemoryByteChannel(arr)
  }

  private def makeMulti(arr: Iterator[Array[Byte]]): SeekableByteChannel = {
    MultiReadOnlySeekableByteChannel.forSeekableByteChannels(arr.map{ makeSingle }.toSeq)
  }
}
