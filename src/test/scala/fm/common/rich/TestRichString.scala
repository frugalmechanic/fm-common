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
    (null: String).toBlankOption should equal (None)
    "".toBlankOption should equal (None)
    "  ".toBlankOption should equal (None)
  }
  
  test("toBlankOption - Some") {
    "asd".toBlankOption should equal (Some("asd"))
  }
  
  test("toIntOption") {
    "123".toIntOption should equal (Some(123))
    "-123".toIntOption should equal (Some(-123))
    
    (null: String).toIntOption should equal (None)
    "".toIntOption should equal (None)
    "foo".toIntOption should equal (None)
    "123.45".toIntOption should equal (None)
    "1234567890000".toIntOption should equal (None) // Too big for Int
    
    "123asd".toIntOption should equal (None)
  }
  
  test("isInt") {
    "123".isInt should equal (true)
    "-123".isInt should equal (true)
    
    (null: String).isInt should equal (false)
    "".isInt should equal (false)
    "foo".isInt should equal (false)
    "123.45".isInt should equal (false)
    "1234567890000".isInt should equal (false) // Too big for Int
    
    "123asd".isInt should equal (false)
  }
  
  test("capitalizeWords") {
    "foo baR".capitalizeWords should equal ("Foo BaR")
    
    "foo_bAR".capitalizeWords('_') should equal ("Foo_BAR")
  }
  
  test("capitalizeFully") {
    "foo baR".capitalizeFully should equal ("Foo Bar")
    
    "foo_bar".capitalizeFully('_') should equal ("Foo_Bar")
  }
  
  test("indexesOf") {
    "aaaaaaaa".indexesOf("aa") should equal (List(0, 2, 4, 6))
    "aaaaaaaa".indexesOf(target = "aa", withinOverlaps = true) should equal (List(0, 1, 2, 3, 4, 5, 6))
    "aaaaaaaa".indexesOf("b") should equal (Nil)
  }
}