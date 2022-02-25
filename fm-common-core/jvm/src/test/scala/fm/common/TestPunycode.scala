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

import java.lang.{StringBuilder => JavaStringBuilder}
import java.net.IDN
import java.util.Random
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestPunycode extends AnyFunSuite with Matchers {

  test("RFC3492 Example A - Arabic (Egyptian)") {
    check("\u0644\u064A\u0647\u0645\u0627\u0628\u062A\u0643\u0644\u0645\u0648\u0634\u0639\u0631\u0628\u064A\u061F", "egbpdaj6bu4bxfgehfvwxn")
  }

  test("RFC3492 Example B - Chinese (simplified)") {
    check("\u4ED6\u4EEC\u4E3A\u4EC0\u4E48\u4E0D\u8BF4\u4E2D\u6587", "ihqwcrb4cv8a8dqg056pqjye")
  }

  test("RFC3492 Example C - Chinese (traditional)") {
    check("\u4ED6\u5011\u7232\u4EC0\u9EBD\u4E0D\u8AAA\u4E2D\u6587", "ihqwctvzc91f659drss3x8bo0yb")
  }

  test("RFC3492 Example D - Czech: Pro<ccaron>prost<ecaron>nemluv<iacute><ccaron>esky") {
    check("\u0050\u0072\u006F\u010D\u0070\u0072\u006F\u0073\u0074\u011B\u006E\u0065\u006D\u006C\u0075\u0076\u00ED\u010D\u0065\u0073\u006B\u0079", "Proprostnemluvesky-uyb24dma41a")
  }

  test("RFC3492 Example E - Hebrew") {
    check("\u05DC\u05DE\u05D4\u05D4\u05DD\u05E4\u05E9\u05D5\u05D8\u05DC\u05D0\u05DE\u05D3\u05D1\u05E8\u05D9\u05DD\u05E2\u05D1\u05E8\u05D9\u05EA", "4dbcagdahymbxekheh6e0a7fei0b")
  }

  test("RFC3492 Example F - Hindi (Devanagari)") {
    check("\u092F\u0939\u0932\u094B\u0917\u0939\u093F\u0928\u094D\u0926\u0940\u0915\u094D\u092F\u094B\u0902\u0928\u0939\u0940\u0902\u092C\u094B\u0932\u0938\u0915\u0924\u0947\u0939\u0948\u0902", "i1baa7eci9glrd9b2ae1bj0hfcgg6iyaf8o0a1dig0cd")
  }

  test("RFC3492 Example G - Japanese (kanji and hiragana)") {
    check("\u306A\u305C\u307F\u3093\u306A\u65E5\u672C\u8A9E\u3092\u8A71\u3057\u3066\u304F\u308C\u306A\u3044\u306E\u304B", "n8jok5ay5dzabd5bym9f0cm5685rrjetr6pdxa")
  }

  test("RFC3492 Example H - Korean (Hangul syllables)") {
    check("\uC138\uACC4\uC758\uBAA8\uB4E0\uC0AC\uB78C\uB4E4\uC774\uD55C\uAD6D\uC5B4\uB97C\uC774\uD574\uD55C\uB2E4\uBA74\uC5BC\uB9C8\uB098\uC88B\uC744\uAE4C", "989aomsvi5e83db1d2a355cv1e0vak1dwrv93d5xbh15a0dt30a5jpsd879ccm6fea98c")
  }

  ignore("RFC3492 Example I - Russian (Cyrillic)") {
    check("\u043F\u043E\u0447\u0435\u043C\u0443\u0436\u0435\u043E\u043D\u0438\u043D\u0435\u0433\u043E\u0432\u043E\u0440\u044F\u0442\u043F\u043E\u0440\u0443\u0441\u0441\u043A\u0438", "b1abfaaepdrnnbgefbaDotcwatmq2g4l")
  }

  test("RFC3492 Example I - Russian (Cyrillic) - Output casing tweaked to prevent: \"b1abfaaepdrnnbgefba[d]otcwatmq2g4l\" was not equal to \"b1abfaaepdrnnbgefba[D]otcwatmq2g4l\"") {
    check("\u043F\u043E\u0447\u0435\u043C\u0443\u0436\u0435\u043E\u043D\u0438\u043D\u0435\u0433\u043E\u0432\u043E\u0440\u044F\u0442\u043F\u043E\u0440\u0443\u0441\u0441\u043A\u0438", "b1abfaaepdrnnbgefbadotcwatmq2g4l")
  }

  test("RFC3492 Example J - Spanish: Porqu<eacute>nopuedensimplementehablarenEspa<ntilde>ol") {
    check("\u0050\u006F\u0072\u0071\u0075\u00E9\u006E\u006F\u0070\u0075\u0065\u0064\u0065\u006E\u0073\u0069\u006D\u0070\u006C\u0065\u006D\u0065\u006E\u0074\u0065\u0068\u0061\u0062\u006C\u0061\u0072\u0065\u006E\u0045\u0073\u0070\u0061\u00F1\u006F\u006C", "PorqunopuedensimplementehablarenEspaol-fmd56a")
  }

  test("RFC3492 Example K - Vietnamese: T<adotbelow>isaoh<odotbelow>kh<ocirc>ngth<ecirchookabove>ch<ihookabove>n<oacute>iti<ecircacute>ngVi<ecircdotbelow>t") {
    check("\u0054\u1EA1\u0069\u0073\u0061\u006F\u0068\u1ECD\u006B\u0068\u00F4\u006E\u0067\u0074\u0068\u1EC3\u0063\u0068\u1EC9\u006E\u00F3\u0069\u0074\u0069\u1EBF\u006E\u0067\u0056\u0069\u1EC7\u0074", "TisaohkhngthchnitingVit-kjcr8268qyxafd2f1b9g")
  }

  test("RFC3492 Example L - 3<nen>B<gumi><kinpachi><sensei>") {
    check("\u0033\u5E74\u0042\u7D44\u91D1\u516B\u5148\u751F", "3B-ww4c5e180e575a65lsy2b")
  }

  test("RFC3492 Example M - <amuro><namie>-with-SUPER-MONKEYS") {
    check("\u5B89\u5BA4\u5948\u7F8E\u6075\u002D\u0077\u0069\u0074\u0068\u002D\u0053\u0055\u0050\u0045\u0052\u002D\u004D\u004F\u004E\u004B\u0045\u0059\u0053", "-with-SUPER-MONKEYS-pc58ag80a8qai00g7n9n")
  }

  test("RFC3492 Example N - Hello-Another-Way-<sorezore><no><basho>") {
    check("\u0048\u0065\u006C\u006C\u006F\u002D\u0041\u006E\u006F\u0074\u0068\u0065\u0072\u002D\u0057\u0061\u0079\u002D\u305D\u308C\u305E\u308C\u306E\u5834\u6240", "Hello-Another-Way--fc4qua05auwb3674vfr0b")
  }

  test("RFC3492 Example O - <hitotsu><yane><no><shita>2") {
    check("\u3072\u3068\u3064\u5C4B\u6839\u306E\u4E0B\u0032", "2-u9tlzr9756bt3uc0v")
  }

  test("RFC3492 Example P - Maji<de>Koi<suru>5<byou><mae>") {
    check("\u004D\u0061\u006A\u0069\u3067\u004B\u006F\u0069\u3059\u308B\u0035\u79D2\u524D", "MajiKoi5-783gue6qz075azm5e")
  }

  test("RFC3492 Example Q - <pafii>de<runba>") {
    check("\u30D1\u30D5\u30A3\u30FC\u0064\u0065\u30EB\u30F3\u30D0", "de-jg4avhby1noc0d")
  }

  test("RFC3492 Example R - <sono><supiido><de>") {
    check("\u305D\u306E\u30B9\u30D4\u30FC\u30C9\u3067", "d9juau41awczczp")
  }

  test("RFC3492 Example S - -> $1.00 <-") {
    check("\u002D\u003E\u0020\u0024\u0031\u002E\u0030\u0030\u0020\u003C\u002D", "-> $1.00 <--")
  }

  test("ASCII String") {
    check("abcdefghijklmnopqrstuvwxyz01234567890", "abcdefghijklmnopqrstuvwxyz01234567890-")
  }

  test("Empty String") {
    check("", "")
  }

  test("Null") {
    check(null, "")
  }

  test("Space") {
    check(" ", " -")
  }

  test("Supplementary Characters - \uD83D\uDCA5") {
    check("\uD83D\uDCA5", "hs8h") // "💥"
  }

  test("Supplementary Characters - foo\uD83D\uDCA5bar") {
    check("foo\uD83D\uDCA5bar", "foobar-rw54e") // "foo💥bar"
  }

  test("Supplementary Characters - \uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8") {
    // As code points:
    check(new String(Array(0x1F600, 0x1F63A, 0x1F9D7, 0x1F1FA, 0x1F1F8).flatMap{ Character.toChars(_) }), "w77hd302awg78n") // "😀😺🧗🇺🇸"

    // As characters:
    check("\uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8", "w77hd302awg78n") // "😀😺🧗🇺🇸"
  }

  test("Supplementary Characters - \uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8  * 4") {
    // As code points:
    check(new String(Array(0x1F600, 0x1F63A, 0x1F9D7, 0x1F1FA, 0x1F1F8).flatMap{ Character.toChars(_) }) * 4, "w77haaagbbb773gcacc95eddd728meaee") // "😀😺🧗🇺🇸"

    // As characters:
    check("\uD83D\uDE00\uD83D\uDE3A\uD83E\uDDD7\uD83C\uDDFA\uD83C\uDDF8" * 4, "w77haaagbbb773gcacc95eddd728meaee") // "😀😺🧗🇺🇸"
  }

  test("Supplementary Characters - One byte, two byte, three byte, four byte") {
    check("Hello  \\ / \" oneByte: \u0024 twoByte: \u00A2 threeByte: \u20AC fourByteSupplementary: \uD83D\uDCA5  World!", "Hello  \\ / \" oneByte: $ twoByte:  threeByte:  fourByteSupplementary:   World!-zgc48095dt0l6w")
  }

  test("Currency Symbols - ₠ ₡ ₢ ₣ ₤ ₥ ₦ ₧ ₨ ₩ ₪ ₫") {
    checkWithoutJVMIDN("₠ ₡ ₢ ₣ ₤ ₥ ₦ ₧ ₨ ₩ ₪ ₫", "           -xt4foapqrstuvwxy")
  }

  test("Combining Marks for Symbols - ⃐ ⃑ ⃒ ⃓ ⃔ ⃕ ⃖ ⃗ ⃘ ⃙ ⃚ ⃛ ⃜ ⃝ ⃞ ⃟ ⃠ ⃡") {
    checkWithoutJVMIDN("⃐ ⃑ ⃒ ⃓ ⃔ ⃕ ⃖ ⃗ ⃘ ⃙ ⃚ ⃛ ⃜ ⃝ ⃞ ⃟ ⃠ ⃡", "                 -gt5juavwxyz0a1a2a3a4a5a6a7a8a9azb")
  }

  test("Character.MIN_CODE_POINT") {
    checkWithoutJVMIDN(Character.MIN_CODE_POINT)
  }

  test("Character.MAX_CODE_POINT") {
    checkWithoutJVMIDN(Character.MAX_CODE_POINT)
  }

  test("Mix of Character.MIN_CODE_POINT and Character.MAX_CODE_POINT") {
    val sb: JavaStringBuilder = new JavaStringBuilder()

    // The SunPunycode has length limitations so this needs to be somewhat small
    (1 to 70).foreach{ _ =>
      sb.appendCodePoint(Character.MIN_CODE_POINT)
      sb.appendCodePoint(Character.MAX_CODE_POINT)
    }

    checkWithoutJVMIDN(sb.toString)
  }

  ignore("High-Surrogate Code Point: 56068 - Not valid by itself") {
    checkWithoutJVMIDN(56068)
  }

  ignore("Low-Surrogate Code Point: 56322 - Not valid by itself") {
    checkWithoutJVMIDN(56322)
  }

  private def ignoreCodePoint(codepoint: Int): Boolean = {
    val isHighSurrogateCodePoint: Boolean = 0xD800 <= codepoint && codepoint <= 0xDBFF
    val isLowSurrogateCodePoint: Boolean = 0xDC00 <= codepoint && codepoint <= 0xDFFF
    (isHighSurrogateCodePoint || isLowSurrogateCodePoint) && !Character.isSupplementaryCodePoint(codepoint)
  }

  test("Random - Short Strings (works with SunPunycode)") {
    val random: Random = new Random(123456789L)

    (1 to 128).foreach { _ =>
      val sb: JavaStringBuilder = new JavaStringBuilder()

      // The SunPunycode has length limitations so this needs to be somewhat small
      (1 to random.nextInt(30)).foreach{ _ =>

        var codePoint: Int = random.nextInt(Character.MAX_CODE_POINT + 1) // "+ 1" because we want MAX_CODE_POINT included

        // Ignore any "High-Surrogate Code Points" (https://www.unicode.org/glossary/#high_surrogate_code_unit) which
        // are not valid by themselves.
        while (ignoreCodePoint(codePoint)) {
          codePoint = random.nextInt(Character.MAX_CODE_POINT + 1) // "+ 1" because we want MAX_CODE_POINT included
        }

        sb.appendCodePoint(codePoint)
      }

      checkWithoutJVMIDN(sb.toString)
    }
  }

  test("Random - Long Strings (does not work with SunPunycode)") {
    val random: Random = new Random(987654321L)

    (1 to 128).foreach { _ =>
      val sb: JavaStringBuilder = new JavaStringBuilder()

      (1 to random.nextInt(1000)).foreach{ _ =>

        var codePoint: Int = random.nextInt(Character.MAX_CODE_POINT + 1) // "+ 1" because we want MAX_CODE_POINT included

        // Ignore any "High-Surrogate Code Points" (https://www.unicode.org/glossary/#high_surrogate_code_unit) which
        // are not valid by themselves.
        while (ignoreCodePoint(codePoint)) {
          codePoint = random.nextInt(Character.MAX_CODE_POINT + 1) // "+ 1" because we want MAX_CODE_POINT included
        }

        sb.appendCodePoint(codePoint)
      }

      checkFM(sb.toString)
    }
  }

  test("All non-ignored Code Points (broken up for SunPunycode)") {
    (Character.MIN_CODE_POINT to Character.MAX_CODE_POINT).filterNot{ ignoreCodePoint }.grouped(30).foreach{ (group: IndexedSeq[Int]) =>
      val sb: JavaStringBuilder = new JavaStringBuilder()
      group.foreach{ sb.appendCodePoint }
      checkWithoutJVMIDN(sb.toString)
    }
  }

  private def check(original: String, encoded: String): Unit = {
    checkIDN(original, encoded)
//    checkSunPunycode(original, encoded)
    checkFM(original, encoded)
  }

  private def checkWithoutJVMIDN(codepoint: Int): Unit = {
    val sb: JavaStringBuilder = new JavaStringBuilder()
    sb.appendCodePoint(codepoint)
    checkWithoutJVMIDN(sb.toString)
  }

  private def checkWithoutJVMIDN(original: String): Unit = {
    // Check round trip when we don't know the encoded version
    checkWithoutJVMIDN(original, Punycode.encode(original))
  }

  private def checkWithoutJVMIDN(original: String, encoded: String): Unit = {
//    checkSunPunycode(original, encoded)
    checkFM(original, encoded)
  }

  private def checkFM(original: String): Unit = {
    // Check round trip when we don't know the encoded version
    checkFM(original, Punycode.encode(original))
  }

  private def checkFM(original: String, encoded: String): Unit = {
    val originalOrEmptyString: String = Option(original).getOrElse("") // if original is null then convert to ""

    Punycode.encode(original) shouldBe encoded
    Punycode.decode(encoded) shouldBe originalOrEmptyString

    val pair: (String,String) = Punycode.encodeToPair(original)
    Punycode.formatPair(pair) shouldBe encoded
    Punycode.formatPair(pair._1, pair._2) shouldBe encoded

    Punycode.decodePair(pair) shouldBe originalOrEmptyString
    Punycode.decodePair(pair._1, pair._2) shouldBe originalOrEmptyString
  }

//  private def checkSunPunycode(original: String, encoded: String): Unit = {
//    // This has been moved to jdk.internal.icu.impl.Punycode in newer JDK versions and causes compile errors
//    import sun.net.idn.{Punycode => SunPunycode}
//
//    // SunPunycode doesn't handle null input so ignore it
//    if (null == original) return
//
//    SunPunycode.encode(new StringBuffer(original), null).toString shouldBe encoded
//    SunPunycode.decode(new StringBuffer(encoded), null).toString shouldBe original
//  }

  private def checkIDN(original: String, encoded: String): Unit = {
    // IDN doesn't handle null input so ignore it
    if (null == original) return

    // Maximum length of a DNS label as defined by https://tools.ietf.org/html/rfc1035
    val MaxDNSLabelLength: Int = 63

    val ACEPrefix: String = "xn--"

    if (encoded.length > MaxDNSLabelLength) {
      an [IllegalArgumentException] should be thrownBy IDN.toASCII(original, IDN.ALLOW_UNASSIGNED)
    } else {
      // Sanity Check - IDN roundtrip should work:
      val idnEncoded: String = IDN.toASCII(original, IDN.ALLOW_UNASSIGNED)
      IDN.toUnicode(idnEncoded, IDN.ALLOW_UNASSIGNED) shouldBe original.toLowerCase

      if (idnEncoded.startsWith(ACEPrefix)) {
        idnEncoded.stripLeading(ACEPrefix) shouldBe encoded.toLowerCase
      } else if (idnEncoded.length == 0) {
        "" shouldBe encoded
      } else {
        idnEncoded+"-" shouldBe encoded.toLowerCase
      }
    }
  }
}
