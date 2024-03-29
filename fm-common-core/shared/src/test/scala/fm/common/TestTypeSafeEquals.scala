/*
 * Copyright 2016 Frugal Mechanic (http://frugalmechanic.com)
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

// Note: Using ≡ and ≠ to avoid conflicts with ScalaTest === method
final class TestTypeSafeEquals extends AnyFunSuite with Matchers {

  test("Basics") {
    1 ≡ 1 shouldBe true
    1 ≠ 1 shouldBe false

    "foo" ≡ "bar" shouldBe false
    "foo" ≠ "bar" shouldBe true
    
    "1d ≡ 1" shouldNot compile
    "1 ≡ 1d" shouldNot compile
    
    "1 ≡ Option(1)" shouldNot compile
    "Option(1) ≡ 1" shouldNot compile
    
    """"foo" ≡ Option("foo")""" shouldNot compile
    """Option("foo") ≡ "foo""" shouldNot compile
  }

  test("More Complex") {
    (1 + 1) ≡ (1 + 1) shouldBe true
    (1 + 1) ≠ (1 + 1) shouldBe false

    ("foo" + "bar") ≡ ("bar" + "foo") shouldBe false
    ("foo" + "bar") ≠ ("bar" + "foo") shouldBe true

    "(1d + 1d) ≡ (1 + 1)" shouldNot compile
    "(1 + 1) ≡ (1d + 1d)" shouldNot compile
  }

  test("nulls") {
    val nullStr: String = null
    val nonNullStr: String = "non-null"

    nullStr ≡ null shouldBe true
    nullStr ≠ null shouldBe false

    nonNullStr ≡ null shouldBe false
    nonNullStr ≠ null shouldBe true

    // Can't get the implicits to work in Scala 2 for these to compile but they work in Scala 3:
//    null ≡ nullStr shouldBe true
//    null ≠ nullStr shouldBe false
//
//    null ≡ null shouldBe true
//    null ≠ null shouldBe false

    """null ≡ 1""" shouldNot compile
    """null ≠ 1""" shouldNot compile
    """1 ≡ null""" shouldNot compile
    """1 ≠ null""" shouldNot compile
  }
  
  test("Subtypes") {
    """1 ≡ Foo("foo")""" shouldNot compile
    """Foo("foo") ≡ 1""" shouldNot compile
    
    Foo("foo") ≡ Foo("bar") shouldBe false
    Foo("foo") ≠ Foo("bar") shouldBe true
    
    val fooAsBase: Base = Foo("foo")
    val foo: Foo = Foo("foo")
    
    val barAsBase: Base = Bar(123)
    val bar: Bar = Bar(123)
    
    fooAsBase ≡ foo shouldBe true
    foo ≡ fooAsBase shouldBe true
    
    fooAsBase ≠ barAsBase shouldBe true
    fooAsBase ≠ bar shouldBe true
    
    "foo ≡ bar" shouldNot compile
    "bar ≡ foo" shouldNot compile
  }
  
  sealed trait Base
  case class Foo(foo: String) extends Base
  case class Bar(bar: Int) extends Base

  //
  // These are for validating the generated bytecode using javap across different Scala versions to make sure the
  // macros generate the same bytecode.
  //

  private val foo: Foo = Foo("foo")
  private val base: Base = Bar(123)

  private def bytecodeCheckEqualsStatic: Boolean = 1 ≡ 1
  private def bytecodeCheckNotEqualsStatic: Boolean = 1 ≠ 1

  private def bytecodeCheckEqualsDynamic: Boolean = foo ≡ base
  private def bytecodeCheckNotEqualsDynamic: Boolean = foo ≠ base
}
