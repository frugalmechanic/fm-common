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

import java.lang.NumberFormatException
import java.math.BigInteger
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import fm.common.Implicits._

class TestRichBigInteger extends AnyFunSuite with Matchers {
  private def bi(s: String): BigInteger = new BigInteger(s)

  test("isZero") {
    bi("0").isZero shouldBe true

    bi("1").isZero shouldBe false
    bi("123").isZero shouldBe false

    assertThrows[NumberFormatException](bi("0.00").isZero)
    assertThrows[NumberFormatException](bi("0.001").isZero)
  }

  test("isNotZero") {
    bi("0").isNotZero shouldBe false

    bi("1").isNotZero shouldBe true
    bi("123").isNotZero shouldBe true

    assertThrows[NumberFormatException](bi("0.00").isNotZero)
    assertThrows[NumberFormatException](bi("0.001").isNotZero)
  }

  test("intValueExactOption") {
    bi("1").intValueExactOption() shouldBe Some(1)
    bi("123").intValueExactOption() shouldBe Some(123)
    null.asInstanceOf[BigInteger].intValueExactOption() shouldBe None

    BigInteger.valueOf(Int.MaxValue).intValueExactOption() shouldBe Some(Int.MaxValue)
    BigInteger.valueOf(Int.MinValue).intValueExactOption() shouldBe Some(Int.MinValue)

    BigInteger.valueOf(Long.MaxValue).intValueExactOption() shouldBe None
    BigInteger.valueOf(Long.MinValue).intValueExactOption() shouldBe None
  }

}
