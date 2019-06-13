/*
 * Copyright 2019 Frugal Mechanic (http://frugalmechanic.com)
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

import java.io.StringReader
import java.lang.{StringBuilder => JavaStringBuilder}
import org.scalatest.{FunSuite, Matchers}

final class TestCodePointReader extends FunSuite with Matchers {

  test("RFC3492 Example A - Arabic (Egyptian)") {
    check("\u0644\u064A\u0647\u0645\u0627\u0628\u062A\u0643\u0644\u0645\u0648\u0634\u0639\u0631\u0628\u064A\u061F")
  }

  test("RFC3492 Example B - Chinese (simplified)") {
    check("\u4ED6\u4EEC\u4E3A\u4EC0\u4E48\u4E0D\u8BF4\u4E2D\u6587")
  }

  test("RFC3492 Example C - Chinese (traditional)") {
    check("\u4ED6\u5011\u7232\u4EC0\u9EBD\u4E0D\u8AAA\u4E2D\u6587")
  }

  test("RFC3492 Example D - Czech: Pro<ccaron>prost<ecaron>nemluv<iacute><ccaron>esky") {
    check("\u0050\u0072\u006F\u010D\u0070\u0072\u006F\u0073\u0074\u011B\u006E\u0065\u006D\u006C\u0075\u0076\u00ED\u010D\u0065\u0073\u006B\u0079")
  }

  test("RFC3492 Example E - Hebrew") {
    check("\u05DC\u05DE\u05D4\u05D4\u05DD\u05E4\u05E9\u05D5\u05D8\u05DC\u05D0\u05DE\u05D3\u05D1\u05E8\u05D9\u05DD\u05E2\u05D1\u05E8\u05D9\u05EA")
  }

  test("RFC3492 Example F - Hindi (Devanagari)") {
    check("\u092F\u0939\u0932\u094B\u0917\u0939\u093F\u0928\u094D\u0926\u0940\u0915\u094D\u092F\u094B\u0902\u0928\u0939\u0940\u0902\u092C\u094B\u0932\u0938\u0915\u0924\u0947\u0939\u0948\u0902")
  }

  test("RFC3492 Example G - Japanese (kanji and hiragana)") {
    check("\u306A\u305C\u307F\u3093\u306A\u65E5\u672C\u8A9E\u3092\u8A71\u3057\u3066\u304F\u308C\u306A\u3044\u306E\u304B")
  }

  test("RFC3492 Example H - Korean (Hangul syllables)") {
    check("\uC138\uACC4\uC758\uBAA8\uB4E0\uC0AC\uB78C\uB4E4\uC774\uD55C\uAD6D\uC5B4\uB97C\uC774\uD574\uD55C\uB2E4\uBA74\uC5BC\uB9C8\uB098\uC88B\uC744\uAE4C")
  }

  test("RFC3492 Example I - Russian (Cyrillic)") {
    check("\u043F\u043E\u0447\u0435\u043C\u0443\u0436\u0435\u043E\u043D\u0438\u043D\u0435\u0433\u043E\u0432\u043E\u0440\u044F\u0442\u043F\u043E\u0440\u0443\u0441\u0441\u043A\u0438")
  }

  test("RFC3492 Example J - Spanish: Porqu<eacute>nopuedensimplementehablarenEspa<ntilde>ol") {
    check("\u0050\u006F\u0072\u0071\u0075\u00E9\u006E\u006F\u0070\u0075\u0065\u0064\u0065\u006E\u0073\u0069\u006D\u0070\u006C\u0065\u006D\u0065\u006E\u0074\u0065\u0068\u0061\u0062\u006C\u0061\u0072\u0065\u006E\u0045\u0073\u0070\u0061\u00F1\u006F\u006C")
  }

  test("RFC3492 Example K - Vietnamese: T<adotbelow>isaoh<odotbelow>kh<ocirc>ngth<ecirchookabove>ch<ihookabove>n<oacute>iti<ecircacute>ngVi<ecircdotbelow>t") {
    check("\u0054\u1EA1\u0069\u0073\u0061\u006F\u0068\u1ECD\u006B\u0068\u00F4\u006E\u0067\u0074\u0068\u1EC3\u0063\u0068\u1EC9\u006E\u00F3\u0069\u0074\u0069\u1EBF\u006E\u0067\u0056\u0069\u1EC7\u0074")
  }

  test("RFC3492 Example L - 3<nen>B<gumi><kinpachi><sensei>") {
    check("\u0033\u5E74\u0042\u7D44\u91D1\u516B\u5148\u751F")
  }

  test("RFC3492 Example M - <amuro><namie>-with-SUPER-MONKEYS") {
    check("\u5B89\u5BA4\u5948\u7F8E\u6075\u002D\u0077\u0069\u0074\u0068\u002D\u0053\u0055\u0050\u0045\u0052\u002D\u004D\u004F\u004E\u004B\u0045\u0059\u0053")
  }

  test("RFC3492 Example N - Hello-Another-Way-<sorezore><no><basho>") {
    check("\u0048\u0065\u006C\u006C\u006F\u002D\u0041\u006E\u006F\u0074\u0068\u0065\u0072\u002D\u0057\u0061\u0079\u002D\u305D\u308C\u305E\u308C\u306E\u5834\u6240")
  }

  test("RFC3492 Example O - <hitotsu><yane><no><shita>2") {
    check("\u3072\u3068\u3064\u5C4B\u6839\u306E\u4E0B\u0032")
  }

  test("RFC3492 Example P - Maji<de>Koi<suru>5<byou><mae>") {
    check("\u004D\u0061\u006A\u0069\u3067\u004B\u006F\u0069\u3059\u308B\u0035\u79D2\u524D")
  }

  test("RFC3492 Example Q - <pafii>de<runba>") {
    check("\u30D1\u30D5\u30A3\u30FC\u0064\u0065\u30EB\u30F3\u30D0")
  }

  test("RFC3492 Example R - <sono><supiido><de>") {
    check("\u305D\u306E\u30B9\u30D4\u30FC\u30C9\u3067")
  }

  test("RFC3492 Example S - -> $1.00 <-") {
    check("\u002D\u003E\u0020\u0024\u0031\u002E\u0030\u0030\u0020\u003C\u002D")
  }

  test("ASCII String") {
    check("abcdefghijklmnopqrstuvwxyz01234567890")
  }

  test("Empty String") {
    check("")
  }

  test("Null") {
    an [NullPointerException] should be thrownBy check(null)
  }

  test("Space") {
    check(" ")
  }

  test("Supplementary Characters - \uD83D\uDCA5") {
    check("\uD83D\uDCA5") // "💥"
  }

  test("Supplementary Characters - foo\uD83D\uDCA5bar") {
    check("foo\uD83D\uDCA5bar") // "foo💥bar"
  }

  test("Supplementary Characters - \uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8") {
    // As code points:
    check(new String(Array(0x1F600, 0x1F63A, 0x1F9D7, 0x1F1FA, 0x1F1F8).flatMap{ Character.toChars(_) })) // "😀😺🧗🇺🇸"

    // As characters:
    check("\uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8") // "😀😺🧗🇺🇸"
  }

  test("Supplementary Characters - \uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8  * 4") {
    // As code points:
    check(new String(Array(0x1F600, 0x1F63A, 0x1F9D7, 0x1F1FA, 0x1F1F8).flatMap{ Character.toChars(_) }) * 4) // "😀😺🧗🇺🇸"

    // As characters:
    check("\uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8" * 4) // "😀😺🧗🇺🇸"
  }

  test("Supplementary Characters - One byte, two byte, three byte, four byte") {
    check("Hello  \\ / \" oneByte: \u0024 twoByte: \u00A2 threeByte: \u20AC fourByteSupplementary: \uD83D\uDCA5  World!")
  }

  test("Currency Symbols - ₠ ₡ ₢ ₣ ₤ ₥ ₦ ₧ ₨ ₩ ₪ ₫") {
    check("₠ ₡ ₢ ₣ ₤ ₥ ₦ ₧ ₨ ₩ ₪ ₫")
  }

  test("Combining Marks for Symbols - ⃐ ⃑ ⃒ ⃓ ⃔ ⃕ ⃖ ⃗ ⃘ ⃙ ⃚ ⃛ ⃜ ⃝ ⃞ ⃟ ⃠ ⃡") {
    check("⃐ ⃑ ⃒ ⃓ ⃔ ⃕ ⃖ ⃗ ⃘ ⃙ ⃚ ⃛ ⃜ ⃝ ⃞ ⃟ ⃠ ⃡")
  }

  test("Character.MIN_CODE_POINT") {
    check(Character.MIN_CODE_POINT)
  }

  test("Character.MAX_CODE_POINT") {
    check(Character.MAX_CODE_POINT)
  }

  test("Mix of Character.MIN_CODE_POINT and Character.MAX_CODE_POINT") {
    val sb: JavaStringBuilder = new JavaStringBuilder()

    // The SunPunycode has length limitations so this needs to be somewhat small
    (1 to 70).foreach{ _ =>
      sb.appendCodePoint(Character.MIN_CODE_POINT)
      sb.appendCodePoint(Character.MAX_CODE_POINT)
    }

    check(sb.toString)
  }

  test("High-Surrogate Code Point: 56068 - Not valid by itself") {
    check(56068)
  }

  test("Repeated High-Surrogate Code Point: 56068 - Not valid by itself") {
    check("\uDB04\uDB04\uDB04\uDB04")
  }

  test("Low-Surrogate Code Point: 56322 - Not valid by itself") {
    check(56322)
  }

  test("Repeated Low-Surrogate Code Point: 56322 - Not valid by itself") {
    check("\uDC02\uDC02\uDC02\uDC02")
  }

  test("Mismatched Supplementary Characters - \uD83D\uDCA5") {
    // \uD83D\uDCA5 is the valid combo for "💥"

    // Unmatched High Surrogate
    check("\uD83D")
    check("\uD83D\uD83D\uD83D")

    // Unmatched Low Surrogate
    check("\uDCA5")
    check("\uDCA5\uDCA5\uDCA5")

    // Reversed Low then High Surrogate
    check("\uDCA5\uD83D")
    check("\uDCA5\uD83D\uDCA5\uD83D\uDCA5\uD83D")

    // High, High, Low
    check("\uD83D\uD83D\uDCA5")

    // High, High, Low, Low
    check("\uD83D\uD83D\uDCA5\uDCA5")

    // Low, High, High, Low
    check("\uDCA5\uD83D\uD83D\uDCA5")

    // Low, High, Low, High
    check("\uDCA5\uD83D\uDCA5\uD83D")
  }

  //
  // Some examples from https://www.cl.cam.ac.uk/~mgk25/ucs/examples/UTF-8-test.txt
  //

  test("UTF-8 decoder capability and stress test - 1") {
    check("You should see the Greek word 'kosme':       \"κόσμε\"")
  }

  test("UTF-8 decoder capability and stress test - 2.1.1") {
    check("2.1.1  1 byte  (U-00000000):        \"�\"")
  }

  test("UTF-8 decoder capability and stress test - 2.1.2") {
    check("2.1.2  2 bytes (U-00000080):        \"\u0080\"")
  }

  test("UTF-8 decoder capability and stress test - 2.1.3") {
    check("2.1.3  3 bytes (U-00000800):        \"ࠀ\"")
  }

  test("UTF-8 decoder capability and stress test - 2.1.4") {
    check("2.1.4  4 bytes (U-00010000):        \"\uD800\uDC00\"")
  }

  test("UTF-8 decoder capability and stress test - 2.1.5") {
    check("2.1.5  5 bytes (U-00200000):        \"�����\"")
  }

  test("UTF-8 decoder capability and stress test - 2.1.6") {
    check("2.1.6  6 bytes (U-04000000):        \"������\"")
  }

  test("UTF-8 decoder capability and stress test - 2.2.1") {
    check("2.2.1  1 byte  (U-0000007F):        \"\u007F\"")
  }

  test("UTF-8 decoder capability and stress test - 2.2.2") {
    check("2.2.2  2 bytes (U-000007FF):        \"\u07FF\"")
  }

  test("UTF-8 decoder capability and stress test - 2.2.3") {
    check("2.2.3  3 bytes (U-0000FFFF):        \"\uFFFF\"")
  }

  test("UTF-8 decoder capability and stress test - 2.2.4") {
    check("2.2.4  4 bytes (U-001FFFFF):        \"����\"")
  }

  test("UTF-8 decoder capability and stress test - 2.2.5") {
    check("2.2.5  5 bytes (U-03FFFFFF):        \"�����\"")
  }

  test("UTF-8 decoder capability and stress test - 2.2.6") {
    check("2.2.6  6 bytes (U-7FFFFFFF):        \"������\"")
  }

  private def check(codepoint: Int): Unit = {
    val sb: JavaStringBuilder = new JavaStringBuilder()
    sb.appendCodePoint(codepoint)
    check(sb.toString)
  }

  private def check(s: String): Unit = {
    checkReadSingle(s, ReaderCodePointReader.GarbageInGarbageOut)
    checkReadSingle(s, ReaderCodePointReader.SkipInvalid)
    checkReadSingle(s, ReaderCodePointReader.Strict)

    (1 to math.min(s.length + 10, 1024)).foreach{ len: Int =>
      checkReadArray(s, len, ReaderCodePointReader.GarbageInGarbageOut)
      checkReadArray(s, len, ReaderCodePointReader.SkipInvalid)
      checkReadArray(s, len, ReaderCodePointReader.Strict)
    }

    (1 to math.min(s.length + 10, 1024)).foreach { len: Int =>
      checkReadMixed(s, ReaderCodePointReader.GarbageInGarbageOut, len)
      checkReadMixed(s, ReaderCodePointReader.SkipInvalid, len)
      checkReadMixed(s, ReaderCodePointReader.Strict, len)
    }
  }

  private def checkReadArray(s: String, size: Int, mode: ReaderCodePointReader.Mode): Unit = withExceptionHandling(s, mode) {
    val reader: CodePointReader = makeReader(s, mode)
    val buf: Array[Int] = new Array(size)
    val sb: JavaStringBuilder = new JavaStringBuilder()

    var numRead: Int = reader.read(buf)

    if (size ≡ 0) {
      numRead shouldBe 0
      return
    }

    while (numRead >= 0) {
      var i: Int = 0
      while (i < numRead) {
        sb.appendCodePoint(buf(i))
        i += 1
      }

      numRead = reader.read(buf)
    }

    val expected: String = mode match {
      case ReaderCodePointReader.GarbageInGarbageOut => s
      case ReaderCodePointReader.SkipInvalid => removeInvalidChars(s)
      case ReaderCodePointReader.Strict => assert(!hasInvalidChars(s)); s
    }

    sb.toString shouldBe expected
  }

  private def checkReadSingle(s: String, mode: ReaderCodePointReader.Mode): Unit = withExceptionHandling(s, mode) {
    val reader: CodePointReader = makeReader(s, mode)

    val expected: String = mode match {
      case ReaderCodePointReader.GarbageInGarbageOut => s
      case ReaderCodePointReader.SkipInvalid => removeInvalidChars(s)
      case ReaderCodePointReader.Strict => s
    }

    val expectedCodePoints: Array[Int] = expected.toCodePointArray

    var cp: Int = reader.read()
    var idx: Int = 0

    while (cp ≠ -1) {
      cp shouldBe expectedCodePoints(idx)

      cp = reader.read()
      idx += 1
    }

    if (mode ≡ ReaderCodePointReader.Strict) assert(!hasInvalidChars(s))
  }

  private def checkReadMixed(s: String, mode: ReaderCodePointReader.Mode, len: Int): Unit = withExceptionHandling(s, mode) {
    val reader: CodePointReader = makeReader(s, mode)

    val sb: JavaStringBuilder = new JavaStringBuilder()

    var cp: Int = reader.read()
    var i: Int = 0

    while (cp ≠ -1) {
      if (cp >= 0) sb.appendCodePoint(cp)

      // Switch between using read() and read(buf) to test interaction of the `hd` variable
      cp = if ((i % 2) ≡ 0) {
        reader.read()
      } else {
        val buf: Array[Int] = new Array(len)
        val numRead: Int = reader.read(buf)

        if (numRead ≡ -1) {
          -1
        } else {
          var i: Int = 0
          while (i < numRead) {
            sb.appendCodePoint(buf(i))
            i += 1
          }
          -2 // Use -2 as a marker not to write anything in the next iteration of the loop
        }
      }

      i += 1
    }

    val expected: String = mode match {
      case ReaderCodePointReader.GarbageInGarbageOut => s
      case ReaderCodePointReader.SkipInvalid => removeInvalidChars(s)
      case ReaderCodePointReader.Strict => s
    }

    sb.toString shouldBe expected

    if (mode ≡ ReaderCodePointReader.Strict) assert(!hasInvalidChars(s))
  }

  private def withExceptionHandling(s: String, mode: ReaderCodePointReader.Mode)(f: => Unit): Unit = {
    if (mode ≡ ReaderCodePointReader.Strict && hasInvalidChars(s)) {
      an [IllegalArgumentException] should be thrownBy f
    } else {
      f
    }
  }

  private def makeReader(s: String, mode: ReaderCodePointReader.Mode): CodePointReader = {
    new ReaderCodePointReader(new StringReader(s), mode)
  }

  private def hasInvalidChars(s: String): Boolean = removeInvalidChars(s) ≠ s

  private def removeInvalidChars(s: String): String = {
    if (s ≡ null) return null

    val sb: JavaStringBuilder = new JavaStringBuilder()
    var i: Int = 0

    while (i < s.length) {
      val ch: Char = s.charAt(i)

      if (ch.isHighSurrogate) {
        if (i + 1 < s.length && s.charAt(i + 1).isLowSurrogate) {
          sb.append(ch)
          sb.append(s.charAt(i + 1))
          i += 1
        } else {
          // Ignore
        }
      } else if (ch.isLowSurrogate) {
        // Skip
      } else {
        sb.append(ch)
      }

      i += 1
    }

    sb.toString
  }
}


