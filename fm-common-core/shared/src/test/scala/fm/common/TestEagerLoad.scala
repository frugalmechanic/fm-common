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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestEagerLoad extends AnyFunSuite with Matchers {

  // Does not currently work
//  EagerLoad.eagerLoadNestedObjects(EagerOuter)

  object EagerOuter {
    var value: String = "outer"

    object EagerInner {
      value = "inner"

      object EagerNestedInner {
        value = "nested_inner"
      }
    }

    EagerLoad.eagerLoadNestedObjects(this)
  }

  test("eagerLoadNestedObjects") {
    EagerOuter.value shouldBe "nested_inner"
  }

  object NonEagerOuter {
    var value: String = "outer"

    object NonEagerInner {
      value = "inner"
    }
  }

  test("non eagerLoadNestedObjects") {
    NonEagerOuter.value shouldBe "outer"
  }
}
