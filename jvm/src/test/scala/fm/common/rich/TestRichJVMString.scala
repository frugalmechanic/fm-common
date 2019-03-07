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
import java.util.Locale
import org.scalatest.{FunSuite, Matchers}

final class TestRichJVMString extends FunSuite with Matchers {
  import fm.common.Implicits._

  test("toLocaleOption") {
    "en-US".toLocaleOption shouldBe Some(Locale.US)
    "es-US".toLocaleOption shouldBe Some(Locale.forLanguageTag("es-US"))
    "foo bar".toLocaleOption shouldBe None
  }

  test("toUnicodeEscapedJavaString") {
    "\u0048\u0065\u006C\u006C\u006F\u002D\u0041\u006E\u006F\u0074\u0068\u0065\u0072\u002D\u0057\u0061\u0079\u002D\u305D\u308C\u305E\u308C\u306E\u5834\u6240".toUnicodeEscapedJavaString shouldBe "\\u0048\\u0065\\u006C\\u006C\\u006F\\u002D\\u0041\\u006E\\u006F\\u0074\\u0068\\u0065\\u0072\\u002D\\u0057\\u0061\\u0079\\u002D\\u305D\\u308C\\u305E\\u308C\\u306E\\u5834\\u6240"
    "\uD83D\uDCA5".toUnicodeEscapedJavaString shouldBe "\\uD83D\\uDCA5" // 💥
    "\uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8".toUnicodeEscapedJavaString shouldBe "\\uD83D\\uDE00\\uD83D\\uDE3A\\uD83E\\uDDD7\\uD83C\\uDDFA\\uD83C\\uDDF8" // "😀😺🧗🇺🇸"
  }

  test("toUnicodeEscapedJavaStringExceptASCII") {
    "abc123".toUnicodeEscapedJavaStringExceptASCII shouldBe "abc123"
    "\u0048\u0065\u006C\u006C\u006F\u002D\u0041\u006E\u006F\u0074\u0068\u0065\u0072\u002D\u0057\u0061\u0079\u002D\u305D\u308C\u305E\u308C\u306E\u5834\u6240".toUnicodeEscapedJavaStringExceptASCII shouldBe "Hello-Another-Way-\\u305D\\u308C\\u305E\\u308C\\u306E\\u5834\\u6240"
    "\uD83D\uDCA5abc\uD83D\uDCA5123\uD83D\uDCA5".toUnicodeEscapedJavaStringExceptASCII shouldBe "\\uD83D\\uDCA5abc\\uD83D\\uDCA5123\\uD83D\\uDCA5"
    "\uD83D\uDCA5".toUnicodeEscapedJavaStringExceptASCII shouldBe "\\uD83D\\uDCA5" // 💥
    "\uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8".toUnicodeEscapedJavaStringExceptASCII shouldBe "\\uD83D\\uDE00\\uD83D\\uDE3A\\uD83E\\uDDD7\\uD83C\\uDDFA\\uD83C\\uDDF8" // "😀😺🧗🇺🇸"
  }
}