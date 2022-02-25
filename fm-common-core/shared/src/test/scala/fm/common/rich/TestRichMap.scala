/*
 * Copyright 2022 Tim Underwood (https://github.com/tpunder)
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
import scala.collection.immutable.SortedMap

class TestRichMap extends AnyFunSuite with Matchers {

  test("mapValuesStrict") {
    Map("one" -> 1, "two" -> 2, "three" -> 3).mapValuesStrict{ _ * 2 } shouldBe Map("one" -> 2, "two" -> 4, "three" -> 6)

    Map(1 -> "1", 2 -> "2", 3 -> "3").mapValuesStrict{ _.toInt } shouldBe Map(1 -> 1, 2 -> 2, 3 -> 3)
  }

  test("toSortedMap") {
    Map("aaa" -> 1, "bbb" -> 2, "ccc" -> 3).toSortedMap shouldBe SortedMap("aaa" -> 1, "bbb" -> 2, "ccc" -> 3)
    Map("ccc" -> 3, "bbb" -> 2, "aaa" -> 1).toSortedMap shouldBe SortedMap("aaa" -> 1, "bbb" -> 2, "ccc" -> 3)

    Map(3 -> "three", 1 -> "one", 2 -> "two").toSortedMap shouldBe SortedMap(1 -> "one", 2 -> "two", 3 -> "three")
  }

  test("toReverseSortedMap") {
    Map("aaa" -> 1, "bbb" -> 2, "ccc" -> 3).toReverseSortedMap shouldBe SortedMap("ccc" -> 3, "bbb" -> 2, "aaa" -> 1)
    Map("ccc" -> 3, "bbb" -> 2, "aaa" -> 1).toReverseSortedMap shouldBe SortedMap("ccc" -> 3, "bbb" -> 2, "aaa" -> 1)

    Map(3 -> "three", 1 -> "one", 2 -> "two").toReverseSortedMap shouldBe SortedMap(3 -> "three", 2 -> "two", 1 -> "one")
  }

}
