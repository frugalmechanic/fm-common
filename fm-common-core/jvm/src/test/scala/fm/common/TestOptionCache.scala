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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestOptionCache extends AnyFunSuite with Matchers {
  test("Boolean") {
    checkBoolean(true)
    checkBoolean(false)
  }

  test("Byte") {
    checkByte(Byte.MinValue)
    checkByte(Byte.MaxValue)

    (Byte.MinValue to Byte.MaxValue).foreach{ (i: Int) => checkByte(i.toByte) }
  }

  test("Char") {
    checkChar(Char.MinValue)
    checkChar(Char.MaxValue)

    (Char.MinValue to Char.MaxValue).foreach{ (ch: Char) => checkChar(ch) }

    (0.toChar to 127.toChar).foreach{ checkChar(_, true) }
  }

  test("Short") {
    checkShort(Short.MinValue)
    checkShort(Short.MaxValue)

    (IntegerCacheUtil.low to IntegerCacheUtil.high).filter{ _.isValidShort }.foreach{ (i: Int) => checkShort(i.toShort, true) }
  }

  test("Int") {
    checkInt(Int.MinValue)
    checkInt(Int.MaxValue)

    (IntegerCacheUtil.low to IntegerCacheUtil.high).foreach{ (i: Int) => checkInt(i, true) }
  }

  test("Long") {
    checkLong(Long.MinValue)
    checkLong(Long.MaxValue)

    checkLong(Int.MinValue.toLong - 1L)
    checkLong(Int.MaxValue.toLong + 1L)

    (IntegerCacheUtil.low to IntegerCacheUtil.high).foreach{ (i: Int) => checkLong(i.toLong, true) }
  }

  private def checkBoolean(v: Boolean): Unit = {
    OptionCache.valueOf(v) shouldBe Some(v)
    Some.cached(v) shouldBe Some(v)

    Some.cached(v) shouldBe theSameInstanceAs (OptionCache.valueOf(v))
  }

  private def checkByte(v: Byte): Unit = {
    OptionCache.valueOf(v) shouldBe Some(v)
    Some.cached(v) shouldBe Some(v)

    Some.cached(v) shouldBe theSameInstanceAs (OptionCache.valueOf(v))
  }

  private def checkChar(v: Char): Unit = checkChar(v, false)

  private def checkChar(v: Char, forceInstanceCheck: Boolean): Unit = {
    OptionCache.valueOf(v) shouldBe Some(v)
    Some.cached(v) shouldBe Some(v)

    if (forceInstanceCheck || v.isASCIIChar) {
      Some.cached(v) shouldBe theSameInstanceAs (OptionCache.valueOf(v))
    }
  }

  private def checkShort(v: Short): Unit = checkShort(v, false)

  private def checkShort(v: Short, forceInstanceCheck: Boolean): Unit = {
    OptionCache.valueOf(v) shouldBe Some(v)
    Some.cached(v) shouldBe Some(v)

    if (forceInstanceCheck || (v >= IntegerCacheUtil.low && v <= IntegerCacheUtil.high)) {
      Some.cached(v) shouldBe theSameInstanceAs (OptionCache.valueOf(v))
    }
  }

  private def checkInt(v: Int): Unit = checkInt(v, false)

  private def checkInt(v: Int, forceInstanceCheck: Boolean): Unit = {
    OptionCache.valueOf(v) shouldBe Some(v)
    Some.cached(v) shouldBe Some(v)

    if (forceInstanceCheck || (v >= IntegerCacheUtil.low && v <= IntegerCacheUtil.high)) {
      Some.cached(v) shouldBe theSameInstanceAs (OptionCache.valueOf(v))
    }
  }

  private def checkLong(v: Long): Unit = checkLong(v, false)

  private def checkLong(v: Long, forceInstanceCheck: Boolean): Unit = {
    OptionCache.valueOf(v) shouldBe Some(v)
    Some.cached(v) shouldBe Some(v)

    if (forceInstanceCheck || (v >= IntegerCacheUtil.low && v <= IntegerCacheUtil.high)) {
      Some.cached(v) shouldBe theSameInstanceAs (OptionCache.valueOf(v))
    }
  }
}
