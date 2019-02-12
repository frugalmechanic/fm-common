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

import org.scalatest.FunSuite
import org.scalatest.Matchers

final class TestRichCharSequence extends FunSuite with Matchers {
  import fm.common.Implicits._
  
  test("isBlank null") {
    null.asInstanceOf[String].isNullOrBlank should equal(true)
  }
  
  test("isBlank whitespace") {
    "".isNullOrBlank should equal(true)
    " ".isNullOrBlank should equal(true)
    "  ".isNullOrBlank should equal(true)
    "\t".isNullOrBlank should equal(true)
    "\n".isNullOrBlank should equal(true)
    "\r".isNullOrBlank should equal(true)
  }
  
  test("isBlank whitespace followed by a character") {
    "a".isNullOrBlank should equal(false)
    " a".isNullOrBlank should equal(false)
    "  a".isNullOrBlank should equal(false)
    "\ta".isNullOrBlank should equal(false)
    "\na".isNullOrBlank should equal(false)
    "\ra".isNullOrBlank should equal(false)
  }
  
  test("isBlank non-empty") {
    "abc".isNullOrBlank should equal(false)
    "123".isNullOrBlank should equal(false)
    "_".isNullOrBlank should equal(false)
    "!".isNullOrBlank should equal(false)
    "@".isNullOrBlank should equal(false)
    "#".isNullOrBlank should equal(false)
  }
  
  test("nextCharsMatch") {
    ",".nextCharsMatch(",", 0) should equal(true)
    ", ".nextCharsMatch(",", 0) should equal(true)

    "".nextCharsMatch(",", 0) should equal(false)
    " ,".nextCharsMatch(",", 0) should equal(false)
    
    "|-|".nextCharsMatch("|-|", 0) should equal(true)
    "|-|asd".nextCharsMatch("|-|", 0) should equal(true)
    
    "".nextCharsMatch("|-|", 0) should equal(false)
    "|".nextCharsMatch("|-|", 0) should equal(false)
    "|-".nextCharsMatch("|-|", 0) should equal(false)
    "|--".nextCharsMatch("|-|", 0) should equal(false)

    " ,".nextCharsMatch(",", 1) should equal(true)
    " a".nextCharsMatch(",", 1) should equal(false)
    " a,".nextCharsMatch(",", 2) should equal(true)

    "".nextCharsMatch("asd", 0) should equal(false)
    "a".nextCharsMatch("asd", 0) should equal(false)
    "as".nextCharsMatch("asd", 0) should equal(false)
    "asf".nextCharsMatch("asd", 0) should equal(false)

    "".nextCharsMatch("", 0) should equal(false)
    "foobar".nextCharsMatch("", 0) should equal(false)

    "".nextCharsMatch(null, 0) should equal(false)
    "foobar".nextCharsMatch(null, 0) should equal(false)
  }

  test("startsWith") {
    "foo".startsWith("foo") should equal(true)
    "foo bar".startsWith("foo") should equal(true)

    "".startsWith("foo") should equal(false)
    "fo".startsWith("foo") should equal(false)
  }
  
  test("indexesOf") {
    "aaaaaaaa".indexesOf("aa", withOverlaps = false) shouldBe List(0, 2, 4, 6)
    "aaaaaaaa".indexesOf(target = "aa", withOverlaps = true) shouldBe List(0, 1, 2, 3, 4, 5, 6)
    "aaaaaaaa".indexesOf("b", withOverlaps = false) shouldBe Nil
  }

  test("equalsNormalized") {
    (null: String).equalsNormalized(null) shouldBe false
    (null: String).equalsNormalized("") shouldBe false
    "".equalsNormalized(null) shouldBe false

    "".equalsNormalized("") shouldBe true
    "foo".equalsNormalized("foo") shouldBe true
    "foo".equalsNormalized(" fOo ") shouldBe true
    "  F o O B a R ".equalsNormalized(" fOo bAr ") shouldBe true
  }

  test("containsIgnoreCase") {
    (null: String).containsIgnoreCase(null) shouldBe false
    (null: String).containsIgnoreCase("") shouldBe false
    "".containsIgnoreCase(null) shouldBe false

    "".containsIgnoreCase("") shouldBe true
    "foo".containsIgnoreCase("foo") shouldBe true
    "FOO".containsIgnoreCase("foo") shouldBe true
    "FOO".containsIgnoreCase("FOO") shouldBe true
    "FoO".containsIgnoreCase("fOo") shouldBe true
    "foo".containsIgnoreCase(" fOo ") shouldBe false
    "  F o O B a R ".containsIgnoreCase(" fOo bAr ") shouldBe false
  }

  test("indexOfNormalized / containsNormalized") {
    checkIndexOfNormalized(null, null, -1)
    checkIndexOfNormalized(null, "", -1)
    checkIndexOfNormalized("", null, -1)

    checkIndexOfNormalized("", "", 0)
    checkIndexOfNormalized("foo", "", 0)
    checkIndexOfNormalized("foo", "foo", 0)
    checkIndexOfNormalized("  F o O B a R ", " fOo bAr ", 2)
  }

  test("indexOfIgnoreCase / containsIgnoreCase") {
    checkIndexOfIgnoreCase(null, null, -1)
    checkIndexOfIgnoreCase(null, "", -1)
    checkIndexOfIgnoreCase("", null, -1)

    checkIndexOfIgnoreCase("", "", 0)
    checkIndexOfIgnoreCase("foo", "", 0)
    checkIndexOfIgnoreCase("foo", "foo", 0)
    checkIndexOfIgnoreCase("  F o O B a R ", " fOo bAr ", -1)

    checkIndexOfIgnoreCase("foo", "FOO", 0)
    checkIndexOfIgnoreCase("FOO", "foo", 0)
    checkIndexOfIgnoreCase("fOo", "FoO", 0)

    checkIndexOfIgnoreCase("  foo  ", "FOO", 2)
    checkIndexOfIgnoreCase("asdasd  FOO  12313!@#!#", "foo", 8)
    checkIndexOfIgnoreCase("!@#!@# fOo!@#!@#!#", "FoO", 7)

    checkIndexOfIgnoreCase("foo", "asdasdfooadasd", -1)
  }

  private def checkIndexOfNormalized(s: CharSequence, target: CharSequence, idx: Int): Unit = {
    s.indexOfNormalized(target) shouldBe idx
    s.containsNormalized(target) shouldBe idx > -1
  }

  private def checkIndexOfIgnoreCase(s: CharSequence, target: CharSequence, idx: Int): Unit = {
    s.indexOfIgnoreCase(target) shouldBe idx
    s.containsIgnoreCase(target) shouldBe idx > -1
  }
}