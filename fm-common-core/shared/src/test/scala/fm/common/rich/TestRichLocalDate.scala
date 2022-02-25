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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import fm.common.Implicits._
import fm.common.{LocalDate, LocalDateCompat}

class TestRichLocalDate extends AnyFunSuite with Matchers {

  test("compare") {
    LocalDateCompat.of(2021, 1, 1) < LocalDateCompat.of(2021, 1, 1) shouldBe false

    LocalDateCompat.of(2021, 1, 1) <= LocalDateCompat.of(2021, 1, 1) shouldBe true
    LocalDateCompat.of(2021, 1, 1) >= LocalDateCompat.of(2021, 1, 1) shouldBe true

    LocalDateCompat.of(2021, 1, 1) < LocalDateCompat.of(2021, 1, 2) shouldBe true
    LocalDateCompat.of(2021, 1, 1) < LocalDateCompat.of(2021, 2, 1) shouldBe true
    LocalDateCompat.of(2021, 1, 1) < LocalDateCompat.of(2022, 1, 1) shouldBe true

    LocalDateCompat.of(2021, 1, 1) > LocalDateCompat.of(2021, 1, 2) shouldBe false
    LocalDateCompat.of(2021, 1, 1) > LocalDateCompat.of(2021, 2, 1) shouldBe false
    LocalDateCompat.of(2021, 1, 1) > LocalDateCompat.of(2022, 1, 1) shouldBe false
  }

  test("toYYYYMMDD") {
    checkYYYYMMDD(0, 1, 1, Some("00000101"), Some(101))
    checkYYYYMMDD(9999, 12, 31, Some("99991231"), Some(99991231))

    checkYYYYMMDD(9, 9, 9, Some("00090909"), Some(90909))
    checkYYYYMMDD(99, 9, 9, Some("00990909"), Some(990909))
    checkYYYYMMDD(999, 9, 9, Some("09990909"), Some(9990909))
    checkYYYYMMDD(9999, 9, 9, Some("99990909"), Some(99990909))

    checkYYYYMMDD(2019, 5, 17, Some("20190517"), Some(20190517))

    checkYYYYMMDD(-1, 1, 1, None, None)
    checkYYYYMMDD(10000, 1, 1, None, None)
  }

  private def checkYYYYMMDD(year: Int, month: Int, day: Int, strOpt: Option[String], intOpt: Option[Int]): Unit = {
    val date: LocalDate = LocalDateCompat.of(year, month, day)

    date.toYYYYMMDDOption shouldBe strOpt
    date.toYYYYMMDDIntOption shouldBe intOpt

    strOpt match {
      case Some(str) => date.toYYYYMMDD shouldBe str
      case None => an [IllegalArgumentException] should be thrownBy date.toYYYYMMDD
    }

    intOpt match {
      case Some(int) => date.toYYYYMMDDInt shouldBe int
      case None => an [IllegalArgumentException] should be thrownBy date.toYYYYMMDDInt
    }
  }
}
