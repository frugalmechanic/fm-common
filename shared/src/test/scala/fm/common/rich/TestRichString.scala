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

import org.scalatest.{FunSuite, Matchers}

final class TestRichString extends FunSuite with Matchers {
  import fm.common.Implicits._
  
  test("toBlankOption - None") {
    (null: String).toBlankOption shouldBe None
    "".toBlankOption shouldBe None
    "  ".toBlankOption shouldBe None
  }
  
  test("toBlankOption - Some") {
    "asd".toBlankOption shouldBe Some("asd")
  }
  
  test("toIntOption") {
    "123".toIntOption shouldBe Some(123)
    "-123".toIntOption shouldBe Some(-123)
    
    (null: String).toIntOption shouldBe None
    "".toIntOption shouldBe None
    "foo".toIntOption shouldBe None
    "123.45".toIntOption shouldBe None
    "1234567890000".toIntOption shouldBe None // Too big for Int
    
    "123asd".toIntOption shouldBe None
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
    "false".toBoolean shouldBe false

    an [IllegalArgumentException] shouldBe thrownBy { (null: String).toBoolean }
    an [IllegalArgumentException] shouldBe thrownBy { "".toBoolean }
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

    "t".parseBoolean shouldBe Some(true)
    "true".parseBoolean shouldBe Some(true)
    "1".parseBoolean shouldBe Some(true)
    "y".parseBoolean shouldBe Some(true)
    "yes".parseBoolean shouldBe Some(true)

    "f".parseBoolean shouldBe Some(false)
    "false".parseBoolean shouldBe Some(false)
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
}