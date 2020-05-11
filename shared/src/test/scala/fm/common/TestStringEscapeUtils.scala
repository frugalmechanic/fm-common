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

final class TestStringEscapeUtils extends AnyFunSuite with Matchers {
  
  test("escapeHTML") {
    h("<foo>") shouldBe "&lt;foo&gt;"
    h("<<<") shouldBe "&lt;&lt;&lt;"
  }

  test("escapeECMAScript") {
    j("http://www.example.com/foo/bar") shouldBe """http:\/\/www.example.com\/foo\/bar"""
    j("Hello\"World") shouldBe "Hello\\\"World"
    j("Multi\nLine") shouldBe "Multi\\nLine"
  }
  
  private def h(s: String): String = StringEscapeUtils.escapeHTML(s)
  private def j(s: String): String = StringEscapeUtils.escapeECMAScript(s)
}
