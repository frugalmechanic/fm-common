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

import org.scalatest.FunSuite
import org.scalatest.Matchers

class TestNormalize extends FunSuite with Matchers {
  
  test("lowerAlphanumericWithSpaces") {
    def t(pair: (String,String)): Unit = TestHelpers.withCallerInfo {
      val (str, urlName) = pair
      Normalize.lowerAlphanumericWithSpaces(str) shouldBe urlName
    }

    t((null: String) -> "")
    t("" -> "")
    t("  " -> "")
    t("-" -> "")
    t(" - " -> "")
    t("Foo" -> "foo")
    t("  Foo  " -> "foo")
    t(" - Foo - " -> "foo")
    t("foo BAR" -> "foo bar")
    t("  foo  BAR  " -> "foo bar")
    t("Dorman HELP!" -> "dorman help")
    t("Dorman HELP\\!" -> "dorman help")
    t("\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5" -> "aaaaaa")
    t("Foo Æ Bar" -> "foo ae bar") // Æ -> ae
    t("Æ Æ Æ" -> "ae ae ae") // Æ -> ae
    t("-Æ" -> "ae") // Æ -> ae
    t("Æ-" -> "ae") // Æ -> ae
    t("-Æ-" -> "ae") // Æ -> ae
    t("Foo \u00F1 Bar" -> "foo n bar")
    t("Foo \u006E\u0303 Bar" -> "foo n bar") // unicode normalization converts "\u006E\u0303" to "\u00F1" which ASCII folding converts to "n"
    t("\uD83D\uDCA5" -> "") // "💥"
  }
  
  test("lowerAlphanumeric") {
    def t(pair: (String,String)): Unit = TestHelpers.withCallerInfo{
      val (str, urlName) = pair
      Normalize.lowerAlphanumeric(str) shouldBe urlName
    }

    t((null: String) -> "")
    t("" -> "")
    t("  " -> "")
    t("-" -> "")
    t(" - " -> "")
    t("Foo" -> "foo")
    t("  Foo  " -> "foo")
    t(" - Foo - " -> "foo")
    t("foo BAR" -> "foobar")
    t("  foo  BAR  " -> "foobar")
    t("Dorman HELP!" -> "dormanhelp")
    t("Dorman HELP\\!" -> "dormanhelp")
    t("dorman HELP\\!" -> "dormanhelp")
    t("doRman HELP\\!" -> "dormanhelp")
    t("\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5" -> "aaaaaa")
    t("FooÆBar" -> "fooaebar") // Æ -> ae
    t("ÆÆÆ" -> "aeaeae") // Æ -> ae
    t("Æ-" -> "ae") // Æ -> ae
    t("-Æ" -> "ae") // Æ -> ae
    t("-Æ-" -> "ae") // Æ -> ae
    t("Foo \u00F1 Bar" -> "foonbar")
    t("Foo \u006E\u0303 Bar" -> "foonbar") // unicode normalization converts "\u006E\u0303" to "\u00F1" which ASCII folding converts to "n"
    t("\uD83D\uDCA5" -> "") // "💥"

    // RFC3492 Example A - Arabic (Egyptian) - Last character gets stripped
    t("\u0644\u064A\u0647\u0645\u0627\u0628\u062A\u0643\u0644\u0645\u0648\u0634\u0639\u0631\u0628\u064A\u061F" -> "\u0644\u064A\u0647\u0645\u0627\u0628\u062A\u0643\u0644\u0645\u0648\u0634\u0639\u0631\u0628\u064A") // Last character gets removed (it must be a symbol?)

    // RFC3492 Example B - Chinese (simplified) - Unmodified
    t("\u4ED6\u4EEC\u4E3A\u4EC0\u4E48\u4E0D\u8BF4\u4E2D\u6587" -> "\u4ED6\u4EEC\u4E3A\u4EC0\u4E48\u4E0D\u8BF4\u4E2D\u6587")

    // RFC3492 Example C - Chinese (traditional) - Unmodified
    t("\u4ED6\u5011\u7232\u4EC0\u9EBD\u4E0D\u8AAA\u4E2D\u6587" -> "\u4ED6\u5011\u7232\u4EC0\u9EBD\u4E0D\u8AAA\u4E2D\u6587")

    // RFC3492 Example D - Czech: Pro<ccaron>prost<ecaron>nemluv<iacute><ccaron>esky - Accents converted to ASCII
    t("\u0050\u0072\u006F\u010D\u0070\u0072\u006F\u0073\u0074\u011B\u006E\u0065\u006D\u006C\u0075\u0076\u00ED\u010D\u0065\u0073\u006B\u0079" -> "procprostenemluvicesky")

    // RFC3492 Example E - Hebrew - Unmodified
    t("\u05DC\u05DE\u05D4\u05D4\u05DD\u05E4\u05E9\u05D5\u05D8\u05DC\u05D0\u05DE\u05D3\u05D1\u05E8\u05D9\u05DD\u05E2\u05D1\u05E8\u05D9\u05EA" -> "\u05DC\u05DE\u05D4\u05D4\u05DD\u05E4\u05E9\u05D5\u05D8\u05DC\u05D0\u05DE\u05D3\u05D1\u05E8\u05D9\u05DD\u05E2\u05D1\u05E8\u05D9\u05EA")

    // RFC3492 Example F - Hindi (Devanagari) - Modified - Some characters are modified and/or stripped
    t("\u092F\u0939\u0932\u094B\u0917\u0939\u093F\u0928\u094D\u0926\u0940\u0915\u094D\u092F\u094B\u0902\u0928\u0939\u0940\u0902\u092C\u094B\u0932\u0938\u0915\u0924\u0947\u0939\u0948\u0902" -> "\u092F\u0939\u0932\u0917\u0939\u0928\u0926\u0915\u092F\u0928\u0939\u092C\u0932\u0938\u0915\u0924\u0939")

    // RFC3492 Example G - Japanese (kanji and hiragana) - Unmodified
    t("\u306A\u305C\u307F\u3093\u306A\u65E5\u672C\u8A9E\u3092\u8A71\u3057\u3066\u304F\u308C\u306A\u3044\u306E\u304B" -> "\u306A\u305C\u307F\u3093\u306A\u65E5\u672C\u8A9E\u3092\u8A71\u3057\u3066\u304F\u308C\u306A\u3044\u306E\u304B")

    // RFC3492 Example H - Korean (Hangul syllables) - Unmodified
    t("\uC138\uACC4\uC758\uBAA8\uB4E0\uC0AC\uB78C\uB4E4\uC774\uD55C\uAD6D\uC5B4\uB97C\uC774\uD574\uD55C\uB2E4\uBA74\uC5BC\uB9C8\uB098\uC88B\uC744\uAE4C" -> "\uC138\uACC4\uC758\uBAA8\uB4E0\uC0AC\uB78C\uB4E4\uC774\uD55C\uAD6D\uC5B4\uB97C\uC774\uD574\uD55C\uB2E4\uBA74\uC5BC\uB9C8\uB098\uC88B\uC744\uAE4C")

    // RFC3492 Example I - Russian (Cyrillic) - Unmodified
    t("\u043F\u043E\u0447\u0435\u043C\u0443\u0436\u0435\u043E\u043D\u0438\u043D\u0435\u0433\u043E\u0432\u043E\u0440\u044F\u0442\u043F\u043E\u0440\u0443\u0441\u0441\u043A\u0438" -> "\u043F\u043E\u0447\u0435\u043C\u0443\u0436\u0435\u043E\u043D\u0438\u043D\u0435\u0433\u043E\u0432\u043E\u0440\u044F\u0442\u043F\u043E\u0440\u0443\u0441\u0441\u043A\u0438")

    // RFC3492 Example J - Spanish: Porqu<eacute>nopuedensimplementehablarenEspa<ntilde>ol - Accents converted to ASCII
    t("\u0050\u006F\u0072\u0071\u0075\u00E9\u006E\u006F\u0070\u0075\u0065\u0064\u0065\u006E\u0073\u0069\u006D\u0070\u006C\u0065\u006D\u0065\u006E\u0074\u0065\u0068\u0061\u0062\u006C\u0061\u0072\u0065\u006E\u0045\u0073\u0070\u0061\u00F1\u006F\u006C" -> "porquenopuedensimplementehablarenespanol")

    // RFC3492 Example K - Vietnamese: T<adotbelow>isaoh<odotbelow>kh<ocirc>ngth<ecirchookabove>ch<ihookabove>n<oacute>iti<ecircacute>ngVi<ecircdotbelow>t - Accents converted to ASCII
    t("\u0054\u1EA1\u0069\u0073\u0061\u006F\u0068\u1ECD\u006B\u0068\u00F4\u006E\u0067\u0074\u0068\u1EC3\u0063\u0068\u1EC9\u006E\u00F3\u0069\u0074\u0069\u1EBF\u006E\u0067\u0056\u0069\u1EC7\u0074" -> "taisaohokhongthechinoitiengviet")

    // RFC3492 Example L - 3<nen>B<gumi><kinpachi><sensei> - The B is lowercased
    t("\u0033\u5E74\u0042\u7D44\u91D1\u516B\u5148\u751F" -> "\u0033\u5E74\u0062\u7D44\u91D1\u516B\u5148\u751F")

    // RFC3492 Example M - <amuro><namie>-with-SUPER-MONKEYS - "-with-SUPER-MONKEYS" is transformed to "withsupermonkeys"
    t("\u5B89\u5BA4\u5948\u7F8E\u6075\u002D\u0077\u0069\u0074\u0068\u002D\u0053\u0055\u0050\u0045\u0052\u002D\u004D\u004F\u004E\u004B\u0045\u0059\u0053" -> "\u5B89\u5BA4\u5948\u7F8E\u6075withsupermonkeys")

    // RFC3492 Example N - Hello-Another-Way-<sorezore><no><basho> - "Hello-Another-Way-" is transformed to "helloanotherway"
    t("\u0048\u0065\u006C\u006C\u006F\u002D\u0041\u006E\u006F\u0074\u0068\u0065\u0072\u002D\u0057\u0061\u0079\u002D\u305D\u308C\u305E\u308C\u306E\u5834\u6240" -> "helloanotherway\u305D\u308C\u305E\u308C\u306E\u5834\u6240")

    // RFC3492 Example O - <hitotsu><yane><no><shita>2 - Unmodified
    t("\u3072\u3068\u3064\u5C4B\u6839\u306E\u4E0B\u0032" -> "\u3072\u3068\u3064\u5C4B\u6839\u306E\u4E0B\u0032")

    // RFC3492 Example P - Maji<de>Koi<suru>5<byou><mae> - Lowercased
    t("\u004D\u0061\u006A\u0069\u3067\u004B\u006F\u0069\u3059\u308B\u0035\u79D2\u524D" -> "\u006D\u0061\u006A\u0069\u3067\u006B\u006F\u0069\u3059\u308B\u0035\u79D2\u524D")

    // RFC3492 Example Q - <pafii>de<runba> - Unmodified
    t("\u30D1\u30D5\u30A3\u30FC\u0064\u0065\u30EB\u30F3\u30D0" -> "\u30D1\u30D5\u30A3\u30FC\u0064\u0065\u30EB\u30F3\u30D0")

    // RFC3492 Example R - <sono><supiido><de> - Unmodified
    t("\u305D\u306E\u30B9\u30D4\u30FC\u30C9\u3067" -> "\u305D\u306E\u30B9\u30D4\u30FC\u30C9\u3067")

    // RFC3492 Example S - -> $1.00 <- - Symbols/spaces stripped
    t("\u002D\u003E\u0020\u0024\u0031\u002E\u0030\u0030\u0020\u003C\u002D" -> "100")
  }
  
  test("lowerAlphanumericWithPositions") {
    def t(str: String, normalized: String, positions: Array[Int]): Unit = TestHelpers.withCallerInfo {
      val res: (String,Array[Int]) = Normalize.lowerAlphanumericWithPositions(str)
      
      (res._1, res._2.toIndexedSeq) shouldBe ((normalized, positions.toIndexedSeq))
    }

    t((null: String), "", Array())
    t("", "", Array())
    t("Foo", "foo", Array(0,1,2))
    t("  Foo  ", "foo", Array(2,3,4))
    t(" - Foo - ", "foo", Array(3,4,5))
    t("foo BAR", "foobar", Array(0,1,2,4,5,6))
    t("  foo  BAR  ", "foobar", Array(2,3,4,7,8,9))
    t("Dorman HELP!", "dormanhelp", Array(0,1,2,3,4,5,7,8,9,10))
    t("Dorman HELP\\!", "dormanhelp", Array(0,1,2,3,4,5,7,8,9,10))
    t("dorman HELP\\!", "dormanhelp", Array(0,1,2,3,4,5,7,8,9,10))
    t("doRman HELP\\!", "dormanhelp", Array(0,1,2,3,4,5,7,8,9,10))
    
    t("foo", "foo", Array(0,1,2))
    t("dormanhelp", "dormanhelp", Array(0,1,2,3,4,5,6,7,8,9))
    t("dorman123help", "dorman123help", Array(0,1,2,3,4,5,6,7,8,9,10,11,12))
    t("\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5", "aaaaaa", Array(0,1,2,3,4,5))

    t("Foo Æ Bar", "fooaebar", Array(0,1,2,4,4,6,7,8)) // Æ -> ae (both with position 4)
    t("Æ Æ Æ", "aeaeae", Array(0,0,2,2,4,4))

    t("Foo \u00F1 Bar", "foonbar", Array(0,1,2,4,6,7,8))
    t("Foo \u006E\u0303 Bar", "foonbar", Array(0,1,2,4,6,7,8)) // Note: positions are only accurate against the normalized unicode input

    t("Foo \u00F1 Bar \u00F1", "foonbarn", Array(0,1,2,4,6,7,8,10))
    t("Foo \u006E\u0303 Bar \u006E\u0303", "foonbarn", Array(0,1,2,4,6,7,8,10)) // Note: positions are only accurate against the normalized unicode input

    t("Foo \u00F1 Æ Bar \u00F1", "foonaebarn", Array(0,1,2,4,6,6,8,9,10,12))
    t("Foo \u006E\u0303 Æ Bar \u006E\u0303", "foonaebarn", Array(0,1,2,4,6,6,8,9,10,12)) // Note: positions are only accurate against the normalized unicode input

    // Supplementary characters should only count as a single character position
    t("\uD83D\uDCA5 Foo \u006E\u0303 Æ Bar \u006E\u0303 \uD83D\uDCA5", "foonaebarn", Array(3,4,5,7,9,9,11,12,13,15)) // Note: positions are only accurate against the normalized unicode input
  }
  
  test("lowerAlphanumeric - Already Normalized - Should eq the original string") {
    def t(str: String): Unit = TestHelpers.withCallerInfo {
      Normalize.lowerAlphanumeric(str) should be theSameInstanceAs(str)
    }
    
    t("")
    t("foo")
    t("dormanhelp")
    t("dorman123help")
  }
  
  test("reverseLowerAlphanumeric") {
    Normalize.reverseLowerAlphanumeric(null, null) shouldBe None
    Normalize.reverseLowerAlphanumeric("", "") shouldBe None
    Normalize.reverseLowerAlphanumeric("Foo B.O.S.C.H. Bar", "bosch") shouldBe Some("B.O.S.C.H.")
    Normalize.reverseLowerAlphanumeric("FooB.O.S.C.H.Bar", "bosch") shouldBe Some("B.O.S.C.H.")
    Normalize.reverseLowerAlphanumeric("B.O.S.C.H. Bar", "bosch") shouldBe Some("B.O.S.C.H.")
    Normalize.reverseLowerAlphanumeric("B.O.S.C.H.Bar", "bosch") shouldBe Some("B.O.S.C.H.")
    Normalize.reverseLowerAlphanumeric("Foo B.O.S.C.H.", "bosch") shouldBe Some("B.O.S.C.H.")
    Normalize.reverseLowerAlphanumeric("FooB.O.S.C.H.", "bosch") shouldBe Some("B.O.S.C.H.")
    Normalize.reverseLowerAlphanumeric("FooB.O.S.C.H. ", "bosch") shouldBe Some("B.O.S.C.H.")
    Normalize.reverseLowerAlphanumeric("B.O.S.C.H.", "bosch") shouldBe Some("B.O.S.C.H.")
    Normalize.reverseLowerAlphanumeric(" B.O.S.C.H. ", "bosch") shouldBe Some("B.O.S.C.H.")
    
    Normalize.reverseLowerAlphanumeric("5S1988", "5s1988") shouldBe Some("5S1988")
    Normalize.reverseLowerAlphanumeric("AIR5S1988", "5s1988") shouldBe Some("5S1988")
    Normalize.reverseLowerAlphanumeric("5S1988AIR", "5s1988") shouldBe Some("5S1988")
    
    Normalize.reverseLowerAlphanumeric("BOSCH .....", "bosch") shouldBe Some("BOSCH")
    Normalize.reverseLowerAlphanumeric("BOSCH.....", "bosch") shouldBe Some("BOSCH.....") // We keep trailing symbols
    Normalize.reverseLowerAlphanumeric("BOSCH -", "bosch") shouldBe Some("BOSCH")
    Normalize.reverseLowerAlphanumeric(" BOSCH - ", "bosch") shouldBe Some("BOSCH")
    Normalize.reverseLowerAlphanumeric(" - BOSCH - ", "bosch") shouldBe Some("BOSCH")
    Normalize.reverseLowerAlphanumeric(" -BOSCH- ", "bosch") shouldBe Some("BOSCH-") // We keep trailing symbols
    Normalize.reverseLowerAlphanumeric(" -BOSCH---- ", "bosch") shouldBe Some("BOSCH----") // We keep trailing symbols
    
    Normalize.reverseLowerAlphanumeric(" - \u00C0\u00C1\u00C2\u00C3\u00C4\u00C5 - ", "aaaaaa") shouldBe Some("\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5")
    Normalize.reverseLowerAlphanumeric(" - -\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5- - ", "aaaaaa") shouldBe Some("\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5-")

    Normalize.reverseLowerAlphanumeric(" - Foo Æ Bar - ", "fooaebar") shouldBe Some("Foo Æ Bar")
    Normalize.reverseLowerAlphanumeric(" - Æ Æ Æ - ", "aeaeae") shouldBe Some("Æ Æ Æ")
    Normalize.reverseLowerAlphanumeric("Æ Æ Æ - ", "ae") shouldBe Some("Æ")
    Normalize.reverseLowerAlphanumeric(" foo - Æ", "ae") shouldBe Some("Æ")

    Normalize.reverseLowerAlphanumeric(" - Foo \u00F1 Bar - ", "foonbar") shouldBe Some("Foo \u00F1 Bar")
    Normalize.reverseLowerAlphanumeric(" - Foo \u006E\u0303 Bar - ", "foonbar") shouldBe Some("Foo \u00F1 Bar") // Reverses to the normalized unicode version only (not the original "\u006E\u0303"

    Normalize.reverseLowerAlphanumeric(" - Foo \u00F1 Bar \u00F1 - ", "foonbarn") shouldBe Some("Foo \u00F1 Bar \u00F1")
    Normalize.reverseLowerAlphanumeric(" - Foo \u006E\u0303 Bar \u006E\u0303 - ", "foonbarn") shouldBe Some("Foo \u00F1 Bar \u00F1")  // Reverses to the normalized unicode version only (not to the original "\u006E\u0303")

    Normalize.reverseLowerAlphanumeric(" - Foo \u00F1 Æ Bar \u00F1 - ", "foonaebarn") shouldBe Some("Foo \u00F1 Æ Bar \u00F1")
    Normalize.reverseLowerAlphanumeric(" - Foo \u006E\u0303 Æ Bar \u006E\u0303 - ", "foonaebarn") shouldBe Some("Foo \u00F1 Æ Bar \u00F1")  // Reverses to the normalized unicode version only (not to the original "\u006E\u0303")
    Normalize.reverseLowerAlphanumeric(" - Æ \u006E\u0303 Æ - ", "aenae") shouldBe Some("Æ \u00F1 Æ")

    Normalize.reverseLowerAlphanumeric("\uD83D\uDCA5 - Æ \u006E\u0303 \uD83D\uDCA5 Æ \uD83D\uDCA5 - ", "aenae") shouldBe Some("Æ \u00F1 \uD83D\uDCA5 Æ")
    Normalize.reverseLowerAlphanumeric("\uD83D\uDCA5 - Æ \uD83D\uDCA5 \u006E\u0303 \uD83D\uDCA5 \uD83D\uDCA5 Æ \uD83D\uDCA5 - ", "aenae") shouldBe Some("Æ \uD83D\uDCA5 \u00F1 \uD83D\uDCA5 \uD83D\uDCA5 Æ")
    Normalize.reverseLowerAlphanumeric("\uD83D\uDCA5 - Æ \u006E\u0303 \uD83D\uDCA5 Æ\uD83D\uDCA5 - ", "aenae") shouldBe Some("Æ \u00F1 \uD83D\uDCA5 Æ\uD83D\uDCA5")
  }
  
  test("urlname") {
    def t(pair: (String,String)): Unit = TestHelpers.withCallerInfo {
      val (str, urlName) = pair
      Normalize.urlName(str) shouldBe urlName
    }

    t((null: String) -> "")
    t("" -> "")
    t("  " -> "")
    t("-" -> "")
    t(" - " -> "")
    t("Foo" -> "foo")
    t("  Foo  " -> "foo")
    t(" - Foo - " -> "foo")
    t("foo BAR" -> "foo-bar")
    t("  foo  BAR  " -> "foo-bar")
    t("Dorman HELP!" -> "dorman-help")
    t("Dorman HELP\\!" -> "dorman-help")
    t("\\\\foo_bar\\asd//\\" -> "foo-bar-asd")
    t("\\\\foo_bar\\\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\\asd//\\" -> "foo-bar-aaaaaa-asd")
  }
  
  test("unicodeNormalization") {
    def t(pair: (String,String)): Unit = TestHelpers.withCallerInfo {
      val (str, urlName) = pair
      Normalize.unicodeNormalization(str) shouldBe urlName
    }
    
    t("" -> "")
    t("foo" -> "foo")
    t("Æ" -> "Æ")

    t("\u006E\u0303" -> "\u00F1") // n~ => ñ
    t("\u00F1-" -> "\u00F1-")
    t(" - Foo \u00F1 Bar \u00F1- " -> " - Foo \u00F1 Bar \u00F1- ")
  }
}
