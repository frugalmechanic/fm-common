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

import org.scalatest.FunSuite
import org.scalatest.Matchers

final class TestImmutableArray extends FunSuite with Matchers {
  test("ImmutableArrayBuilder - int") {
    val builder: ImmutableArrayBuilder[Int] = ImmutableArray.newBuilder

    builder.size shouldBe 0
    an [ArrayIndexOutOfBoundsException] should be thrownBy builder(-1)
    an [ArrayIndexOutOfBoundsException] should be thrownBy builder(0)
    an [ArrayIndexOutOfBoundsException] should be thrownBy builder(1)
    builder.result shouldBe ImmutableArray.empty

    builder += 1
    builder.size shouldBe 1
    builder(0) shouldBe 1
    builder.result shouldBe ImmutableArray(1)

    builder(9) = 9
    builder.size shouldBe 10
    builder.result shouldBe ImmutableArray(1,0,0,0,0,0,0,0,0,9)
    builder(0) shouldBe 1
    builder(9) shouldBe 9

    builder.sizeHint(100)
    an [ArrayIndexOutOfBoundsException] should be thrownBy builder(-1)
    an [ArrayIndexOutOfBoundsException] should be thrownBy builder(50)
    an [ArrayIndexOutOfBoundsException] should be thrownBy builder(99)
    an [ArrayIndexOutOfBoundsException] should be thrownBy builder(100)
    builder.size shouldBe 10
    builder.result shouldBe ImmutableArray(1,0,0,0,0,0,0,0,0,9)

    builder(25) = 25
    builder.size shouldBe 26
    builder.result shouldBe ImmutableArray(1,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,25)

    builder.insert(0, 99)
    builder.size shouldBe 27
    builder.result shouldBe ImmutableArray(99,1,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,25)

    builder.insert(5, 88)
    builder.size shouldBe 28
    builder.result shouldBe ImmutableArray(99,1,0,0,0,88,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,25)

    builder.insert(27, 77)
    builder.size shouldBe 29
    builder.result shouldBe ImmutableArray(99,1,0,0,0,88,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,77,25)

    builder.insert(29, 66)
    builder.size shouldBe 30
    builder.result shouldBe ImmutableArray(99,1,0,0,0,88,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,77,25,66)

    builder.clear()
    builder.size shouldBe 0
    builder.result shouldBe ImmutableArray.empty
  }
}
