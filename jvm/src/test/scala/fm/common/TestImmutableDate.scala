/*
 * Copyright 2018 Frugal Mechanic (http://frugalmechanic.com)
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
import org.scalatest.{FunSuite, Matchers}

final class TestImmutableDate extends FunSuite with Matchers {

  private val date: Date = new Date()
  private val immutableDate: ImmutableDate = ImmutableDate(date)
  private val instant: Instant = date.toInstant

  test("Basics") {
    immutableDate.getTime shouldBe date.getTime
    immutableDate.millis shouldBe date.getTime
  }

  test("Date => ImmutableDate - non-null") {
    (date: ImmutableDate) shouldBe immutableDate
  }

  test("ImmutableDate => Date - non-null") {
    (immutableDate: Date) shouldBe date
  }

  test("ImmutableDate => Instant - non-null") {
    (immutableDate: Instant) shouldBe instant
  }

  test("apply with non-null Date") {
    (ImmutableDate(date)) shouldBe immutableDate
  }

  test("apply with non-null Instant") {
    (ImmutableDate(instant)) shouldBe immutableDate
  }

  test("ImmutableDate => Date Implicit - null") {
    ((null: ImmutableDate): Date) shouldBe null
  }

  test("Date => ImmutableDate Implicit - null") {
    ((null: Date): ImmutableDate) shouldBe null
  }

  test("ImmutableDate => Instant Implicit - null") {
    ((null: ImmutableDate): Instant) shouldBe null
  }

  test("apply with null Date") {
    (ImmutableDate(null: Date)) shouldBe null
  }

  test("apply with null Instant") {
    (ImmutableDate(null: Instant)) shouldBe null
  }
}
