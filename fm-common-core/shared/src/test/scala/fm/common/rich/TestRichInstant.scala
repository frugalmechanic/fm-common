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
import fm.common.{Instant, InstantCompat}

class TestRichInstant extends AnyFunSuite with Matchers {

  test("<") {
    InstantCompat.ofEpochMilli(1) < InstantCompat.ofEpochMilli(2) shouldBe true
    
    InstantCompat.ofEpochMilli(1) < InstantCompat.ofEpochMilli(1) shouldBe false
    InstantCompat.ofEpochMilli(2) < InstantCompat.ofEpochMilli(1) shouldBe false
  }

  test(">") {
    InstantCompat.ofEpochMilli(2) > InstantCompat.ofEpochMilli(1) shouldBe true

    InstantCompat.ofEpochMilli(1) > InstantCompat.ofEpochMilli(2) shouldBe false
    InstantCompat.ofEpochMilli(1) > InstantCompat.ofEpochMilli(1) shouldBe false
  }
}
