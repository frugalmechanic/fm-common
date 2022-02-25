/*
 * Copyright 2014 Frugal Mechanic (http://frugalmechanic.com)
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

final class TestRichString extends AnyFunSuite with Matchers {
  import fm.common.Implicits._
  
  test("toBlankOption - None") {
    (null: String).toBlankOption shouldBe None
    "".toBlankOption shouldBe None
    "  ".toBlankOption shouldBe None
  }
  
  test("toBlankOption - Some") {
    "asd".toBlankOption shouldBe Some("asd")
  }
  
  test("toIntOptionCached") {
    "123".toIntOptionCached shouldBe Some(123)
    "-123".toIntOptionCached shouldBe Some(-123)
    
    (null: String).toIntOptionCached shouldBe None
    "".toIntOptionCached shouldBe None
    "foo".toIntOptionCached shouldBe None
    "123.45".toIntOptionCached shouldBe None
    "1234567890000".toIntOptionCached shouldBe None // Too big for Int
    
    "123asd".toIntOptionCached shouldBe None
  }
  
  test("isInt") {
    "123".isInt shouldBe true
    "-123".isInt shouldBe true
    
    (null: String).isInt shouldBe false
    "".isInt shouldBe false
    "foo".isInt shouldBe false
    "123.45".isInt shouldBe false
    "1234567890000".isInt shouldBe false // Too big for Int
    
    "123asd".isInt shouldBe false
  }

  test("toBoolean") {
    "true".toBoolean shouldBe true
    "TrUe".toBoolean shouldBe true
    "false".toBoolean shouldBe false
    "fAlSe".toBoolean shouldBe false

    an [IllegalArgumentException] shouldBe thrownBy { (null: String).toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { " true".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "false ".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "t".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "f".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "yes".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "y".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "no".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "n".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "0".toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "1".toBoolean }
  }

  test("parseBoolean") {
    (null: String).parseBoolean shouldBe None
    "".parseBoolean shouldBe None
    "\n".parseBoolean shouldBe None
    "\t".parseBoolean shouldBe None
    " ".parseBoolean shouldBe None
    " foo ".parseBoolean shouldBe None
    " bar ".parseBoolean shouldBe None

    "t".parseBoolean shouldBe Some(true)
    "true".parseBoolean shouldBe Some(true)
    " \nTrUe\n ".parseBoolean shouldBe Some(true)
    "1".parseBoolean shouldBe Some(true)
    "y".parseBoolean shouldBe Some(true)
    "yes".parseBoolean shouldBe Some(true)

    "f".parseBoolean shouldBe Some(false)
    "false".parseBoolean shouldBe Some(false)
    " \tfALSe\t ".parseBoolean shouldBe Some(false)
    "0".parseBoolean shouldBe Some(false)
    "n".parseBoolean shouldBe Some(false)
    "no".parseBoolean shouldBe Some(false)
  }
  
  test("capitalizeWords") {
    "foo baR".capitalizeWords shouldBe "Foo BaR"
    
    "foo_bAR".capitalizeWords('_') shouldBe "Foo_BAR"
  }
  
  test("capitalizeFully") {
    "foo baR".capitalizeFully shouldBe "Foo Bar"
    
    "foo_bar".capitalizeFully('_') shouldBe "Foo_Bar"
  }

  test("lPad") {
    "".lPad(0, ' ') shouldBe ""
    "".lPad(1, ' ') shouldBe " "
    "".lPad(2, ' ') shouldBe "  "
    "".lPad(3, ' ') shouldBe "   "
    "".lPad(4, ' ') shouldBe "    "
    "".lPad(5, ' ') shouldBe "     "

    "A".lPad(0, 'B') shouldBe "A"
    "A".lPad(1, 'B') shouldBe "A"
    "A".lPad(2, 'B') shouldBe "BA"
    "A".lPad(3, 'B') shouldBe "BBA"
    "A".lPad(4, 'B') shouldBe "BBBA"
    "A".lPad(5, 'B') shouldBe "BBBBA"
  }

  test("rPad") {
    "".rPad(0, ' ') shouldBe ""
    "".rPad(1, ' ') shouldBe " "
    "".rPad(2, ' ') shouldBe "  "
    "".rPad(3, ' ') shouldBe "   "
    "".rPad(4, ' ') shouldBe "    "
    "".rPad(5, ' ') shouldBe "     "

    "A".rPad(0, 'B') shouldBe "A"
    "A".rPad(1, 'B') shouldBe "A"
    "A".rPad(2, 'B') shouldBe "AB"
    "A".rPad(3, 'B') shouldBe "ABB"
    "A".rPad(4, 'B') shouldBe "ABBB"
    "A".rPad(5, 'B') shouldBe "ABBBB"
  }

  // Alias of rPad
  test("pad") {
    "".pad(0, ' ') shouldBe ""
    "".pad(1, ' ') shouldBe " "
    "".pad(2, ' ') shouldBe "  "
    "".pad(3, ' ') shouldBe "   "
    "".pad(4, ' ') shouldBe "    "
    "".pad(5, ' ') shouldBe "     "

    "A".pad(0, 'B') shouldBe "A"
    "A".pad(1, 'B') shouldBe "A"
    "A".pad(2, 'B') shouldBe "AB"
    "A".pad(3, 'B') shouldBe "ABB"
    "A".pad(4, 'B') shouldBe "ABBB"
    "A".pad(5, 'B') shouldBe "ABBBB"
  }

  test("startsWithIgnoreCase") {
    (null: String).startsWithIgnoreCase(null) shouldBe false
    (null: String).startsWithIgnoreCase("") shouldBe false
    (null: String).startsWithIgnoreCase("foo") shouldBe false
    "".startsWithIgnoreCase(null) shouldBe false
    "foo".startsWithIgnoreCase(null) shouldBe false

    "".startsWithIgnoreCase("foo") shouldBe false
    "bar".startsWithIgnoreCase("foo") shouldBe false
    "foo".startsWithIgnoreCase("foobar") shouldBe false

    "".startsWithIgnoreCase("") shouldBe true
    "foo".startsWithIgnoreCase("") shouldBe true
    "foo".startsWithIgnoreCase("f") shouldBe true
    "foo".startsWithIgnoreCase("fo") shouldBe true
    "foo".startsWithIgnoreCase("foo") shouldBe true
    "foobar".startsWithIgnoreCase("foo") shouldBe true

    "FoO".startsWithIgnoreCase("") shouldBe true
    "FoO".startsWithIgnoreCase("f") shouldBe true
    "FoO".startsWithIgnoreCase("fO") shouldBe true
    "FoO".startsWithIgnoreCase("fOo") shouldBe true
    "FoObar".startsWithIgnoreCase("fOo") shouldBe true
  }

  test("endsWithIgnoreCase") {
    (null: String).endsWithIgnoreCase(null) shouldBe false
    (null: String).endsWithIgnoreCase("") shouldBe false
    (null: String).endsWithIgnoreCase("foo") shouldBe false
    "".endsWithIgnoreCase(null) shouldBe false
    "foo".endsWithIgnoreCase(null) shouldBe false

    "".endsWithIgnoreCase("foo") shouldBe false
    "bar".endsWithIgnoreCase("foo") shouldBe false
    "foo".endsWithIgnoreCase("foobar") shouldBe false

    "".endsWithIgnoreCase("") shouldBe true
    "foo".endsWithIgnoreCase("") shouldBe true
    "foo".endsWithIgnoreCase("o") shouldBe true
    "foo".endsWithIgnoreCase("oo") shouldBe true
    "foo".endsWithIgnoreCase("foo") shouldBe true
    "foobar".endsWithIgnoreCase("bar") shouldBe true

    "FoO".endsWithIgnoreCase("") shouldBe true
    "FoO".endsWithIgnoreCase("O") shouldBe true
    "FoO".endsWithIgnoreCase("oO") shouldBe true
    "FoO".endsWithIgnoreCase("fOo") shouldBe true
    "FoObar".endsWithIgnoreCase("bar") shouldBe true
  }
}