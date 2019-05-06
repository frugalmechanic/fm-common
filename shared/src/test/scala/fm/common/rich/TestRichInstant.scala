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
package fm.common.rich

import java.time.Instant
import org.scalatest.{FunSuite, Matchers}

final class TestRichInstant extends FunSuite with Matchers {
  import fm.common.Implicits._

  test("atEndOfDay") {
    val endOfDay: Instant = Instant.parse("2015-11-19T23:59:59.999999999Z")

    Instant.MIN.atEndOfDay shouldBe Instant.parse("-1000000000-01-01T23:59:59.999999999Z")
    Instant.MAX.atEndOfDay shouldBe Instant.parse("+1000000000-12-31T23:59:59.999999999Z")

    Instant.parse("2015-11-19T23:59:59.999999999Z").atEndOfDay shouldBe endOfDay
    Instant.parse("2015-11-19T00:00:00.000000000Z").atEndOfDay shouldBe endOfDay
    Instant.parse("2015-11-19T12:12:12.555555555Z").atEndOfDay shouldBe endOfDay
  }

  test("atStartOfDay") {
    val startOfDay: Instant = Instant.parse("2015-11-19T00:00:00.000Z")

    Instant.parse("2015-11-19T23:59:59.999999999Z").atStartOfDay shouldBe startOfDay
    Instant.parse("2015-11-19T00:00:00.000000000Z").atStartOfDay shouldBe startOfDay
    Instant.parse("2015-11-19T12:12:12.555555555Z").atStartOfDay shouldBe startOfDay

    Instant.MIN.atStartOfDay shouldBe Instant.parse("-1000000000-01-01T00:00:00.000000000Z")
    Instant.MAX.atStartOfDay shouldBe Instant.parse("+1000000000-12-31T00:00:00.000000000Z")

  }

}