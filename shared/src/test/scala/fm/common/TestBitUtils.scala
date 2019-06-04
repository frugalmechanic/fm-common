/*
 * Copyright 2016 Frugal Mechanic (http://frugalmechanic.com)
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

import org.scalatest.FunSuite
import org.scalatest.Matchers

final class TestBitUtils extends FunSuite with Matchers {

  private def checkLong(a: Int, b: Int, res: Long): Unit = TestHelpers.withCallerInfo{
    BitUtils.makeLong(a, b) shouldBe res
    BitUtils.getUpper(res) shouldBe a
    BitUtils.getLower(res) shouldBe b
    BitUtils.splitLong(res) shouldBe ((a, b))
  }
  
  test("makeLong") {
    checkLong(0, 0, 0)
    checkLong(Int.MinValue, Int.MinValue, -9223372034707292160L)
    checkLong(Int.MaxValue, Int.MaxValue, 9223372034707292159L)
    
    checkLong(Int.MinValue, Int.MaxValue, -9223372034707292161L)
    checkLong(Int.MaxValue, Int.MinValue, 9223372034707292160L)
    
    checkLong(0, Int.MinValue, 2147483648L)
    checkLong(0, Int.MaxValue, 2147483647L)
    
    checkLong(Int.MinValue, 0, -9223372036854775808L)
    checkLong(Int.MaxValue, 0, 9223372032559808512L)
    
    checkLong(1, 1, 4294967297L)
    checkLong(-1, -1, -1)
    checkLong(-1, 1, -4294967295L)
    checkLong(1, -1, 8589934591L)
  }


  private def checkInt(a: Short, b: Short, res: Int): Unit = TestHelpers.withCallerInfo{
    BitUtils.makeInt(a, b) shouldBe res
    BitUtils.getUpper(res) shouldBe a
    BitUtils.getLower(res) shouldBe b
    BitUtils.splitInt(res) shouldBe ((a, b))
  }

  test("makeInt") {
    checkInt(0, 0, 0)
    checkInt(Short.MinValue, Short.MinValue, -2147450880)
    checkInt(Short.MaxValue, Short.MaxValue, 2147450879)

    checkInt(Short.MinValue, Short.MaxValue, -2147450881)
    checkInt(Short.MaxValue, Short.MinValue, 2147450880)

    checkInt(0, Short.MinValue, 32768)
    checkInt(0, Short.MaxValue, 32767)

    checkInt(Short.MinValue, 0, -2147483648)
    checkInt(Short.MaxValue, 0, 2147418112)

    checkInt(1, 1, 65537)
    checkInt(-1, -1, -1)
    checkInt(-1, 1, -65535)
    checkInt(1, -1, 131071)
  }

  private def checkIntWithUpper24Bits(a: Int, b: Short, res: Int): Unit = TestHelpers.withCallerInfo{
    BitUtils.makeIntWithUpper24Bits(a, b) shouldBe res
    BitUtils.getUpper24Bits(res) shouldBe a
    BitUtils.getLower8Bits(res) shouldBe b
  }

  test("makeIntWithUpper24Bits") {
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithUpper24Bits(0xffffff + 1, 0)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithUpper24Bits(Int.MaxValue, 0)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithUpper24Bits(Int.MinValue, 0)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithUpper24Bits(0, (0xff + 1).toShort)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithUpper24Bits(0, Short.MaxValue)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithUpper24Bits(0, Short.MinValue)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithUpper24Bits(-1, 0)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithUpper24Bits(0, -1)

    checkIntWithUpper24Bits(0, 0, 0)
    checkIntWithUpper24Bits(0xffffff, 0, 0xffffff00)
    checkIntWithUpper24Bits(0xffffff, 0xff.toShort, 0xffffffff)
    checkIntWithUpper24Bits(0, 0xff.toShort, 0xff)
    checkIntWithUpper24Bits(1, 0, 0x100)
    checkIntWithUpper24Bits(1, 1, 0x101)
    checkIntWithUpper24Bits(0, 1, 1)
  }

  private def checkIntWithLower24Bits(a: Short, b: Int, res: Int): Unit = TestHelpers.withCallerInfo{
    BitUtils.makeIntWithLower24Bits(a, b) shouldBe res
    BitUtils.getUpper8Bits(res) shouldBe a
    BitUtils.getLower24Bits(res) shouldBe b
  }

  test("makeIntWithLower24Bits") {
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithLower24Bits((0xff + 1).toShort, 0)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithLower24Bits(Short.MaxValue, 0)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithLower24Bits(Short.MinValue, 0)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithLower24Bits(0, 0xffffff + 1)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithLower24Bits(0, Int.MaxValue)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithLower24Bits(0, Int.MinValue)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithLower24Bits(-1, 0)
    an [IllegalArgumentException] should be thrownBy BitUtils.makeIntWithLower24Bits(0, -1)

    checkIntWithLower24Bits(0, 0, 0)
    checkIntWithLower24Bits(0, 0xffffff, 0xffffff)
    checkIntWithLower24Bits(0xff.toShort, 0xffffff, 0xffffffff)
    checkIntWithLower24Bits(0xff.toShort, 0, 0xff000000)
    checkIntWithLower24Bits(1, 0, 0x1000000)
    checkIntWithLower24Bits(1, 1, 0x1000001)
    checkIntWithLower24Bits(0, 1, 1)
  }
}
