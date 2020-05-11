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

import java.time.Instant
import java.util.Date
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestUUID extends AnyFunSuite with Matchers {
  test("UUID.Zero") {
    checkSpecial(UUID(0L, 0L), UUID.Zero, _.isZero)
  }

  test("UUID.UnsignedMaxValue") {
    checkSpecial(UUID(-1L, -1L), UUID.UnsignedMaxValue, _.isUnsignedMaxValue)
  }

  test("UUID.SignedMaxValue") {
    checkSpecial(UUID(Long.MinValue, Long.MinValue), UUID.SignedMinValue, _.isSignedMinValue)
  }

  test("UUID.SignedMinValue") {
    checkSpecial(UUID(Long.MaxValue, Long.MaxValue), UUID.SignedMaxValue, _.isSignedMaxValue)
  }

  test("Encoding/Decoding") {
    check(0, 0, 0, 0)
    check(1, 1, 1, 1)
    check(1, 2, 3, 4)
    check(1, 1, -1, 1)
    
    check(0, 0, Short.MinValue, 0)
    check(0xffffffffffffL, Short.MaxValue, Short.MaxValue, 0xffffffffffffL)
    check(0xffffffffffffL, 0, 0, 0xffffffffffffL)
    
    check(123456789L, 30321, 12345, 987654321L)
    check(123456789L, 30321, -12345, 987654321L)

    check(UUID.Zero)
    check(UUID(0, Long.MaxValue))
    check(UUID(0, Long.MinValue))
    check(UUID(Long.MaxValue, 0))
    check(UUID(Long.MinValue, 0))
    check(UUID(Long.MaxValue, Long.MaxValue))
    check(UUID(Long.MinValue, Long.MinValue))
    check(UUID(Long.MaxValue, Long.MinValue))
    check(UUID(Long.MinValue, Long.MaxValue))
  }
  
  test("Invalid Timestamp Values") {
    checkInvalidArgument(-1, 123, 456, 789)
    checkInvalidArgument(Long.MinValue, 123, 456, 789)
    checkInvalidArgument(Long.MaxValue, 123, 456, 789)
    checkInvalidArgument(0xffffffffffffL + 1, 123, 456, 789)
  }
  
  test("Invalid Counter Values") {
    checkInvalidArgument(123, -1, 456, 789)
    checkInvalidArgument(123, Int.MinValue, 456, 789)
    checkInvalidArgument(123, Int.MinValue, 456, 789)
    checkInvalidArgument(123, 65536, 456, 789)
  }
  
  test("Invalid NodeId Values") {
    checkInvalidArgument(123, 456, Short.MinValue - 1, 789)
    checkInvalidArgument(123, 456, Short.MaxValue + 1, 789)
    checkInvalidArgument(123, 456, Int.MinValue, 789)
    checkInvalidArgument(123, 456, Int.MaxValue, 789)
  }
  
  test("Invalid Random Values") {
    checkInvalidArgument(123, 456, 789, -1)
    checkInvalidArgument(123, 456, 789, 0xffffffffffffL + 1)
    checkInvalidArgument(123, 456, 789, Long.MinValue)
    checkInvalidArgument(123, 456, 789, Long.MaxValue)
  }

  test("checkEpochMilli") {
    checkEpochMilli(0L) // Min Value
    checkEpochMilli(281474976710655L) // Max 6-byte value
    checkEpochMilli(System.currentTimeMillis())
  }
  
  test("Random Values") {
    (0 until 1000000).foreach{ i => checkRandom() }
  }

  private def checkRandom(): Unit = check(UUID())

  private def checkSpecial(value: UUID, expected: UUID, isSpecial: UUID => Boolean): Unit = {
    check(value)
    value shouldBe expected
    isSpecial(expected) shouldBe true
    isSpecial(value) shouldBe true
  }

  private def check(uuid: UUID): Unit = {
    check(uuid.epochMilli, uuid.counter, uuid.nodeId, uuid.random, uuid)
  }

  private def checkEpochMilli(epochMilli: Long): Unit = TestHelpers.withCallerInfo {
    checkEpochMilli(epochMilli, UUID.forEpochMilli(epochMilli))
    checkEpochMilli(epochMilli, UUID(ImmutableDate(epochMilli)))
    checkEpochMilli(epochMilli, UUID(new Date(epochMilli)))
    checkEpochMilli(epochMilli, UUID(Instant.ofEpochMilli(epochMilli)))
  }

  private def checkEpochMilli(epochMilli: Long, uuid: UUID): Unit = TestHelpers.withCallerInfo {
    // Note: We pass in the original epochMilli into this since that is what we want to check
    check(epochMilli, uuid.counter, uuid.nodeId, uuid.random)
  }
  
  private def check(epochMilli: Long, counter: Int, nodeId: Int, random: Long): Unit = TestHelpers.withCallerInfo {
    def go(uuidToCheck: UUID): Unit = check(epochMilli, counter, nodeId, random, uuidToCheck)

    val uuid: UUID = UUID(epochMilli, counter, nodeId, random)

    go(uuid)
    go(UUID(uuid.toByteArray()))
    go(UUID(uuid.toBigInt))
    go(UUID(uuid.toBigInteger))
    go(UUID(uuid.toHex))
    go(UUID(uuid.toBase16))
    go(UUID(uuid.toBase58))
    go(UUID(uuid.toBase64))
//    go(UUID(uuid.toBase64NoPadding)) // Note: Some of these conflict with Base58 for decoding
    go(UUID(uuid.toBase64URL))
//    go(UUID(uuid.toBase64URLNoPadding)) // Note: Some of these conflict with Base58 for decoding
    go(UUID(uuid.toPrettyString))
    go(UUID(uuid.toPrettyString('_')))
    go(UUID(uuid.toPrettyString(':')))
    go(UUID(uuid.toPrettyString('?')))
    go(UUID(uuid.toStandardString))
    go(UUID(uuid.toStandardString('_')))
    go(UUID(uuid.toStandardString(':')))
    go(UUID(uuid.toStandardString('?')))
    go(UUID(uuid.toString))
    go(UUID(uuid.toJavaUUID))
    go(UUID(uuid.toJavaUUID.toString))
  }
  
  private def check(epochMilli: Long, counter: Int, nodeId: Int, random: Long, uuid: UUID): Unit = TestHelpers.withCallerInfo {
    withClue ("epochMilli:") { uuid.epochMilli shouldBe epochMilli }
    withClue ("counter:") { uuid.counter shouldBe counter }
    withClue ("nodeId:") { uuid.nodeId shouldBe nodeId }
    withClue ("random:") { uuid.random shouldBe random }
  }
  
  private def checkInvalidArgument(epochMilli: Long, counter: Int, nodeId: Int, random: Long): Unit = TestHelpers.withCallerInfo{
    an [IllegalArgumentException] should be thrownBy { check(epochMilli, counter, nodeId, random) }
  }
}
