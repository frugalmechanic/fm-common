// Generated Tue Feb 05 21:28:01 PST 2019
// AUTO-GENERATED FROM THE makeAccents.sh SCRIPT
// AUTO-GENERATED FROM THE makeAccents.sh SCRIPT
// AUTO-GENERATED FROM THE makeAccents.sh SCRIPT
// AUTO-GENERATED FROM THE makeAccents.sh SCRIPT

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

import java.lang.{StringBuilder => JavaStringBuilder}
import scala.annotation.switch

object ASCIIUtil {
  val MaxASCIIChar: Char = '\u007F' // 127
  val MaxASCIICodePoint: Int = MaxASCIIChar.toInt

  /**
   * The maximum number of ASCII characters a single Unicode character will expand to
   *
   * All examples that expands to 4 characters:
   *
   *   '\u247D' => "(10)"  // ⑽  [PARENTHESIZED NUMBER TEN]
   *   '\u247E' => "(11)"  // ⑾  [PARENTHESIZED NUMBER ELEVEN]
   *   '\u247F' => "(12)"  // ⑿  [PARENTHESIZED NUMBER TWELVE]
   *   '\u2480' => "(13)"  // ⒀  [PARENTHESIZED NUMBER THIRTEEN]
   *   '\u2481' => "(14)"  // ⒁  [PARENTHESIZED NUMBER FOURTEEN]
   *   '\u2482' => "(15)"  // ⒂  [PARENTHESIZED NUMBER FIFTEEN]
   *   '\u2483' => "(16)"  // ⒃  [PARENTHESIZED NUMBER SIXTEEN]
   *   '\u2484' => "(17)"  // ⒄  [PARENTHESIZED NUMBER SEVENTEEN]
   *   '\u2485' => "(18)"  // ⒅  [PARENTHESIZED NUMBER EIGHTEEN]
   *   '\u2486' => "(19)"  // ⒆  [PARENTHESIZED NUMBER NINETEEN]
   *   '\u2487' => "(20)"  // ⒇  [PARENTHESIZED NUMBER TWENTY]
   */
  val MaxASCIIExpandedLength: Int = 4
  
  /**
   * Converts Accented Characters to the Non-Accented Equivalent Char.
   * 
   * Note: This only works for when there is a 1 to 1 Character equivalence (i.e. it does not work for stuff like Æ which needs to expand to AE)
   */
  def toASCIIChar(c: Char): Char = {
    // This is potentially more JIT friendly since the JVM should be able
    // to inline this method and will almost always hit the common case
    // of just returning the original character.  The slower path will be
    // calling stripAccentCharImpl()
    if (c < '\u0080') c else stripAccentCharImpl(c)
  }
  
  /**
   * Converts Accented Characters to the Non-Accented Equivalent String (or null if already ASCII or no conversion exists).
   */
  def toASCIICharsOrNull(c: Char): String = {
    if (c < '\u0080') null else stripAccentStringImplOrNull(c)
  }
  
  /**
   * Converts Accented Characters to the Non-Accented Equivalent String.
   *
   * Note: This expands stuff like Æ to AE)
   */
  def convertToASCII(s: String): String = {
    if (null == s) return ""

    var i: Int = 0

    // Skip past any ASCII characters
    while (i < s.length && s.charAt(i) < '\u0080'){
      i += 1
    }

    // If we made it through the entire string then there are no accents
    // otherwise we need to switch to convertToASCIIStartingAt
    if (i == s.length) s else convertToASCIIStartingAt(s, i)
  }

  private def convertToASCIIStartingAt(s: String, idx: Int): String = {
    val sb: JavaStringBuilder = new JavaStringBuilder(s.length)

    // Add anything up to idx
    sb.append(s, 0, idx)

    var i: Int = idx

    while (i < s.length) {
      appendASCIIString(s.charAt(i), sb)
      i += 1
    }

    sb.toString()
  }
  
  /**
   * Converts Accented Characters to the Non-Accented Equivalent String.
   */
  private def appendASCIIString(c: Char, sb: JavaStringBuilder): Unit = {
    // This is potentially more JIT friendly since the JVM should be able
    // to inline this method and will almost always hit the common case
    // of just returning the original character.  The slower path will be
    // calling stripAccentStringImpl()
    if (c < '\u0080') {
      sb.append(c)
    } else {
      val str: String = stripAccentStringImplOrNull(c)
      if (null == str) sb.append(c) else sb.append(str)
    }
  }
  
    /** Generated From Lucene's ASCIIFoldingFilter.java */
  private def stripAccentCharImpl(c: Char): Char = {
    // Quick test: if it's not in range then just keep current character
    if (c < '\u0080') {
      c
    } else {
      (c: @switch) match {

        // ASCII: !

        // ！  [FULLWIDTH EXCLAMATION MARK]
        case '\uFF01' => '!'

        // ASCII: "

        // «  [LEFT-POINTING DOUBLE ANGLE QUOTATION MARK]
        // »  [RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK]
        // “  [LEFT DOUBLE QUOTATION MARK]
        // ”  [RIGHT DOUBLE QUOTATION MARK]
        // „  [DOUBLE LOW-9 QUOTATION MARK]
        // ″  [DOUBLE PRIME]
        // ‶  [REVERSED DOUBLE PRIME]
        // ❝  [HEAVY DOUBLE TURNED COMMA QUOTATION MARK ORNAMENT]
        // ❞  [HEAVY DOUBLE COMMA QUOTATION MARK ORNAMENT]
        // ❮  [HEAVY LEFT-POINTING ANGLE QUOTATION MARK ORNAMENT]
        // ❯  [HEAVY RIGHT-POINTING ANGLE QUOTATION MARK ORNAMENT]
        // ＂  [FULLWIDTH QUOTATION MARK]
        case '\u00AB' | '\u00BB' | '\u201C' | '\u201D' | '\u201E' | '\u2033' | '\u2036' | '\u275D' | '\u275E' | '\u276E' | '\u276F' | '\uFF02' => '"'

        // ASCII: #

        // ＃  [FULLWIDTH NUMBER SIGN]
        case '\uFF03' => '#'

        // ASCII: $

        // ＄  [FULLWIDTH DOLLAR SIGN]
        case '\uFF04' => '$'

        // ASCII: %

        // ⁒  [COMMERCIAL MINUS SIGN]
        // ％  [FULLWIDTH PERCENT SIGN]
        case '\u2052' | '\uFF05' => '%'

        // ASCII: &

        // ＆  [FULLWIDTH AMPERSAND]
        case '\uFF06' => '&'

        // ASCII: (

        // ⁽  [SUPERSCRIPT LEFT PARENTHESIS]
        // ₍  [SUBSCRIPT LEFT PARENTHESIS]
        // ❨  [MEDIUM LEFT PARENTHESIS ORNAMENT]
        // ❪  [MEDIUM FLATTENED LEFT PARENTHESIS ORNAMENT]
        // （  [FULLWIDTH LEFT PARENTHESIS]
        case '\u207D' | '\u208D' | '\u2768' | '\u276A' | '\uFF08' => '('

        // ASCII: )

        // ⁾  [SUPERSCRIPT RIGHT PARENTHESIS]
        // ₎  [SUBSCRIPT RIGHT PARENTHESIS]
        // ❩  [MEDIUM RIGHT PARENTHESIS ORNAMENT]
        // ❫  [MEDIUM FLATTENED RIGHT PARENTHESIS ORNAMENT]
        // ）  [FULLWIDTH RIGHT PARENTHESIS]
        case '\u207E' | '\u208E' | '\u2769' | '\u276B' | '\uFF09' => ')'

        // ASCII: *

        // ⁎  [LOW ASTERISK]
        // ＊  [FULLWIDTH ASTERISK]
        case '\u204E' | '\uFF0A' => '*'

        // ASCII: +

        // ⁺  [SUPERSCRIPT PLUS SIGN]
        // ₊  [SUBSCRIPT PLUS SIGN]
        // ＋  [FULLWIDTH PLUS SIGN]
        case '\u207A' | '\u208A' | '\uFF0B' => '+'

        // ASCII: ,

        // ，  [FULLWIDTH COMMA]
        case '\uFF0C' => ','

        // ASCII: -

        // ‘  [LEFT SINGLE QUOTATION MARK]
        // ’  [RIGHT SINGLE QUOTATION MARK]
        // ‚  [SINGLE LOW-9 QUOTATION MARK]
        // ‛  [SINGLE HIGH-REVERSED-9 QUOTATION MARK]
        // ′  [PRIME]
        // ‵  [REVERSED PRIME]
        // ‹  [SINGLE LEFT-POINTING ANGLE QUOTATION MARK]
        // ›  [SINGLE RIGHT-POINTING ANGLE QUOTATION MARK]
        // ❛  [HEAVY SINGLE TURNED COMMA QUOTATION MARK ORNAMENT]
        // ❜  [HEAVY SINGLE COMMA QUOTATION MARK ORNAMENT]
        // ＇  [FULLWIDTH APOSTROPHE]
        // ‐  [HYPHEN]
        // ‑  [NON-BREAKING HYPHEN]
        // ‒  [FIGURE DASH]
        // –  [EN DASH]
        // —  [EM DASH]
        // ⁻  [SUPERSCRIPT MINUS]
        // ₋  [SUBSCRIPT MINUS]
        // －  [FULLWIDTH HYPHEN-MINUS]
        case '\u2018' | '\u2019' | '\u201A' | '\u201B' | '\u2032' | '\u2035' | '\u2039' | '\u203A' | '\u275B' | '\u275C' | '\uFF07' | '\u2010' | '\u2011' | '\u2012' | '\u2013' | '\u2014' | '\u207B' | '\u208B' | '\uFF0D' => '-'

        // ASCII: .

        // ．  [FULLWIDTH FULL STOP]
        case '\uFF0E' => '.'

        // ASCII: /

        // ⁄  [FRACTION SLASH]
        // ／  [FULLWIDTH SOLIDUS]
        case '\u2044' | '\uFF0F' => '/'

        // ASCII: 0

        // ⁰  [SUPERSCRIPT ZERO]
        // ₀  [SUBSCRIPT ZERO]
        // ⓪  [CIRCLED DIGIT ZERO]
        // ⓿  [NEGATIVE CIRCLED DIGIT ZERO]
        // ０  [FULLWIDTH DIGIT ZERO]
        case '\u2070' | '\u2080' | '\u24EA' | '\u24FF' | '\uFF10' => '0'

        // ASCII: 1

        // ¹  [SUPERSCRIPT ONE]
        // ₁  [SUBSCRIPT ONE]
        // ①  [CIRCLED DIGIT ONE]
        // ⓵  [DOUBLE CIRCLED DIGIT ONE]
        // ❶  [DINGBAT NEGATIVE CIRCLED DIGIT ONE]
        // ➀  [DINGBAT CIRCLED SANS-SERIF DIGIT ONE]
        // ➊  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT ONE]
        // １  [FULLWIDTH DIGIT ONE]
        case '\u00B9' | '\u2081' | '\u2460' | '\u24F5' | '\u2776' | '\u2780' | '\u278A' | '\uFF11' => '1'

        // ASCII: 2

        // ²  [SUPERSCRIPT TWO]
        // ₂  [SUBSCRIPT TWO]
        // ②  [CIRCLED DIGIT TWO]
        // ⓶  [DOUBLE CIRCLED DIGIT TWO]
        // ❷  [DINGBAT NEGATIVE CIRCLED DIGIT TWO]
        // ➁  [DINGBAT CIRCLED SANS-SERIF DIGIT TWO]
        // ➋  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT TWO]
        // ２  [FULLWIDTH DIGIT TWO]
        case '\u00B2' | '\u2082' | '\u2461' | '\u24F6' | '\u2777' | '\u2781' | '\u278B' | '\uFF12' => '2'

        // ASCII: 3

        // ³  [SUPERSCRIPT THREE]
        // ₃  [SUBSCRIPT THREE]
        // ③  [CIRCLED DIGIT THREE]
        // ⓷  [DOUBLE CIRCLED DIGIT THREE]
        // ❸  [DINGBAT NEGATIVE CIRCLED DIGIT THREE]
        // ➂  [DINGBAT CIRCLED SANS-SERIF DIGIT THREE]
        // ➌  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT THREE]
        // ３  [FULLWIDTH DIGIT THREE]
        case '\u00B3' | '\u2083' | '\u2462' | '\u24F7' | '\u2778' | '\u2782' | '\u278C' | '\uFF13' => '3'

        // ASCII: 4

        // ⁴  [SUPERSCRIPT FOUR]
        // ₄  [SUBSCRIPT FOUR]
        // ④  [CIRCLED DIGIT FOUR]
        // ⓸  [DOUBLE CIRCLED DIGIT FOUR]
        // ❹  [DINGBAT NEGATIVE CIRCLED DIGIT FOUR]
        // ➃  [DINGBAT CIRCLED SANS-SERIF DIGIT FOUR]
        // ➍  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FOUR]
        // ４  [FULLWIDTH DIGIT FOUR]
        case '\u2074' | '\u2084' | '\u2463' | '\u24F8' | '\u2779' | '\u2783' | '\u278D' | '\uFF14' => '4'

        // ASCII: 5

        // ⁵  [SUPERSCRIPT FIVE]
        // ₅  [SUBSCRIPT FIVE]
        // ⑤  [CIRCLED DIGIT FIVE]
        // ⓹  [DOUBLE CIRCLED DIGIT FIVE]
        // ❺  [DINGBAT NEGATIVE CIRCLED DIGIT FIVE]
        // ➄  [DINGBAT CIRCLED SANS-SERIF DIGIT FIVE]
        // ➎  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FIVE]
        // ５  [FULLWIDTH DIGIT FIVE]
        case '\u2075' | '\u2085' | '\u2464' | '\u24F9' | '\u277A' | '\u2784' | '\u278E' | '\uFF15' => '5'

        // ASCII: 6

        // ⁶  [SUPERSCRIPT SIX]
        // ₆  [SUBSCRIPT SIX]
        // ⑥  [CIRCLED DIGIT SIX]
        // ⓺  [DOUBLE CIRCLED DIGIT SIX]
        // ❻  [DINGBAT NEGATIVE CIRCLED DIGIT SIX]
        // ➅  [DINGBAT CIRCLED SANS-SERIF DIGIT SIX]
        // ➏  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SIX]
        // ６  [FULLWIDTH DIGIT SIX]
        case '\u2076' | '\u2086' | '\u2465' | '\u24FA' | '\u277B' | '\u2785' | '\u278F' | '\uFF16' => '6'

        // ASCII: 7

        // ⁷  [SUPERSCRIPT SEVEN]
        // ₇  [SUBSCRIPT SEVEN]
        // ⑦  [CIRCLED DIGIT SEVEN]
        // ⓻  [DOUBLE CIRCLED DIGIT SEVEN]
        // ❼  [DINGBAT NEGATIVE CIRCLED DIGIT SEVEN]
        // ➆  [DINGBAT CIRCLED SANS-SERIF DIGIT SEVEN]
        // ➐  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SEVEN]
        // ７  [FULLWIDTH DIGIT SEVEN]
        case '\u2077' | '\u2087' | '\u2466' | '\u24FB' | '\u277C' | '\u2786' | '\u2790' | '\uFF17' => '7'

        // ASCII: 8

        // ⁸  [SUPERSCRIPT EIGHT]
        // ₈  [SUBSCRIPT EIGHT]
        // ⑧  [CIRCLED DIGIT EIGHT]
        // ⓼  [DOUBLE CIRCLED DIGIT EIGHT]
        // ❽  [DINGBAT NEGATIVE CIRCLED DIGIT EIGHT]
        // ➇  [DINGBAT CIRCLED SANS-SERIF DIGIT EIGHT]
        // ➑  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT EIGHT]
        // ８  [FULLWIDTH DIGIT EIGHT]
        case '\u2078' | '\u2088' | '\u2467' | '\u24FC' | '\u277D' | '\u2787' | '\u2791' | '\uFF18' => '8'

        // ASCII: 9

        // ⁹  [SUPERSCRIPT NINE]
        // ₉  [SUBSCRIPT NINE]
        // ⑨  [CIRCLED DIGIT NINE]
        // ⓽  [DOUBLE CIRCLED DIGIT NINE]
        // ❾  [DINGBAT NEGATIVE CIRCLED DIGIT NINE]
        // ➈  [DINGBAT CIRCLED SANS-SERIF DIGIT NINE]
        // ➒  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT NINE]
        // ９  [FULLWIDTH DIGIT NINE]
        case '\u2079' | '\u2089' | '\u2468' | '\u24FD' | '\u277E' | '\u2788' | '\u2792' | '\uFF19' => '9'

        // ASCII: :

        // ：  [FULLWIDTH COLON]
        case '\uFF1A' => ':'

        // ASCII: ;

        // ⁏  [REVERSED SEMICOLON]
        // ；  [FULLWIDTH SEMICOLON]
        case '\u204F' | '\uFF1B' => ';'

        // ASCII: <

        // ❬  [MEDIUM LEFT-POINTING ANGLE BRACKET ORNAMENT]
        // ❰  [HEAVY LEFT-POINTING ANGLE BRACKET ORNAMENT]
        // ＜  [FULLWIDTH LESS-THAN SIGN]
        case '\u276C' | '\u2770' | '\uFF1C' => '<'

        // ASCII: =

        // ⁼  [SUPERSCRIPT EQUALS SIGN]
        // ₌  [SUBSCRIPT EQUALS SIGN]
        // ＝  [FULLWIDTH EQUALS SIGN]
        case '\u207C' | '\u208C' | '\uFF1D' => '='

        // ASCII: >

        // ❭  [MEDIUM RIGHT-POINTING ANGLE BRACKET ORNAMENT]
        // ❱  [HEAVY RIGHT-POINTING ANGLE BRACKET ORNAMENT]
        // ＞  [FULLWIDTH GREATER-THAN SIGN]
        case '\u276D' | '\u2771' | '\uFF1E' => '>'

        // ASCII: ?

        // ？  [FULLWIDTH QUESTION MARK]
        case '\uFF1F' => '?'

        // ASCII: @

        // ＠  [FULLWIDTH COMMERCIAL AT]
        case '\uFF20' => '@'

        // ASCII: A

        // À  [LATIN CAPITAL LETTER A WITH GRAVE]
        // Á  [LATIN CAPITAL LETTER A WITH ACUTE]
        // Â  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX]
        // Ã  [LATIN CAPITAL LETTER A WITH TILDE]
        // Ä  [LATIN CAPITAL LETTER A WITH DIAERESIS]
        // Å  [LATIN CAPITAL LETTER A WITH RING ABOVE]
        // Ā  [LATIN CAPITAL LETTER A WITH MACRON]
        // Ă  [LATIN CAPITAL LETTER A WITH BREVE]
        // Ą  [LATIN CAPITAL LETTER A WITH OGONEK]
        // Ə  http://en.wikipedia.org/wiki/Schwa  [LATIN CAPITAL LETTER SCHWA]
        // Ǎ  [LATIN CAPITAL LETTER A WITH CARON]
        // Ǟ  [LATIN CAPITAL LETTER A WITH DIAERESIS AND MACRON]
        // Ǡ  [LATIN CAPITAL LETTER A WITH DOT ABOVE AND MACRON]
        // Ǻ  [LATIN CAPITAL LETTER A WITH RING ABOVE AND ACUTE]
        // Ȁ  [LATIN CAPITAL LETTER A WITH DOUBLE GRAVE]
        // Ȃ  [LATIN CAPITAL LETTER A WITH INVERTED BREVE]
        // Ȧ  [LATIN CAPITAL LETTER A WITH DOT ABOVE]
        // Ⱥ  [LATIN CAPITAL LETTER A WITH STROKE]
        // ᴀ  [LATIN LETTER SMALL CAPITAL A]
        // Ḁ  [LATIN CAPITAL LETTER A WITH RING BELOW]
        // Ạ  [LATIN CAPITAL LETTER A WITH DOT BELOW]
        // Ả  [LATIN CAPITAL LETTER A WITH HOOK ABOVE]
        // Ấ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND ACUTE]
        // Ầ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND GRAVE]
        // Ẩ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE]
        // Ẫ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND TILDE]
        // Ậ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND DOT BELOW]
        // Ắ  [LATIN CAPITAL LETTER A WITH BREVE AND ACUTE]
        // Ằ  [LATIN CAPITAL LETTER A WITH BREVE AND GRAVE]
        // Ẳ  [LATIN CAPITAL LETTER A WITH BREVE AND HOOK ABOVE]
        // Ẵ  [LATIN CAPITAL LETTER A WITH BREVE AND TILDE]
        // Ặ  [LATIN CAPITAL LETTER A WITH BREVE AND DOT BELOW]
        // Ⓐ  [CIRCLED LATIN CAPITAL LETTER A]
        // Ａ  [FULLWIDTH LATIN CAPITAL LETTER A]
        case '\u00C0' | '\u00C1' | '\u00C2' | '\u00C3' | '\u00C4' | '\u00C5' | '\u0100' | '\u0102' | '\u0104' | '\u018F' | '\u01CD' | '\u01DE' | '\u01E0' | '\u01FA' | '\u0200' | '\u0202' | '\u0226' | '\u023A' | '\u1D00' | '\u1E00' | '\u1EA0' | '\u1EA2' | '\u1EA4' | '\u1EA6' | '\u1EA8' | '\u1EAA' | '\u1EAC' | '\u1EAE' | '\u1EB0' | '\u1EB2' | '\u1EB4' | '\u1EB6' | '\u24B6' | '\uFF21' => 'A'

        // ASCII: B

        // Ɓ  [LATIN CAPITAL LETTER B WITH HOOK]
        // Ƃ  [LATIN CAPITAL LETTER B WITH TOPBAR]
        // Ƀ  [LATIN CAPITAL LETTER B WITH STROKE]
        // ʙ  [LATIN LETTER SMALL CAPITAL B]
        // ᴃ  [LATIN LETTER SMALL CAPITAL BARRED B]
        // Ḃ  [LATIN CAPITAL LETTER B WITH DOT ABOVE]
        // Ḅ  [LATIN CAPITAL LETTER B WITH DOT BELOW]
        // Ḇ  [LATIN CAPITAL LETTER B WITH LINE BELOW]
        // Ⓑ  [CIRCLED LATIN CAPITAL LETTER B]
        // Ｂ  [FULLWIDTH LATIN CAPITAL LETTER B]
        case '\u0181' | '\u0182' | '\u0243' | '\u0299' | '\u1D03' | '\u1E02' | '\u1E04' | '\u1E06' | '\u24B7' | '\uFF22' => 'B'

        // ASCII: C

        // Ç  [LATIN CAPITAL LETTER C WITH CEDILLA]
        // Ć  [LATIN CAPITAL LETTER C WITH ACUTE]
        // Ĉ  [LATIN CAPITAL LETTER C WITH CIRCUMFLEX]
        // Ċ  [LATIN CAPITAL LETTER C WITH DOT ABOVE]
        // Č  [LATIN CAPITAL LETTER C WITH CARON]
        // Ƈ  [LATIN CAPITAL LETTER C WITH HOOK]
        // Ȼ  [LATIN CAPITAL LETTER C WITH STROKE]
        // ʗ  [LATIN LETTER STRETCHED C]
        // ᴄ  [LATIN LETTER SMALL CAPITAL C]
        // Ḉ  [LATIN CAPITAL LETTER C WITH CEDILLA AND ACUTE]
        // Ⓒ  [CIRCLED LATIN CAPITAL LETTER C]
        // Ｃ  [FULLWIDTH LATIN CAPITAL LETTER C]
        case '\u00C7' | '\u0106' | '\u0108' | '\u010A' | '\u010C' | '\u0187' | '\u023B' | '\u0297' | '\u1D04' | '\u1E08' | '\u24B8' | '\uFF23' => 'C'

        // ASCII: D

        // Ð  [LATIN CAPITAL LETTER ETH]
        // Ď  [LATIN CAPITAL LETTER D WITH CARON]
        // Đ  [LATIN CAPITAL LETTER D WITH STROKE]
        // Ɖ  [LATIN CAPITAL LETTER AFRICAN D]
        // Ɗ  [LATIN CAPITAL LETTER D WITH HOOK]
        // Ƌ  [LATIN CAPITAL LETTER D WITH TOPBAR]
        // ᴅ  [LATIN LETTER SMALL CAPITAL D]
        // ᴆ  [LATIN LETTER SMALL CAPITAL ETH]
        // Ḋ  [LATIN CAPITAL LETTER D WITH DOT ABOVE]
        // Ḍ  [LATIN CAPITAL LETTER D WITH DOT BELOW]
        // Ḏ  [LATIN CAPITAL LETTER D WITH LINE BELOW]
        // Ḑ  [LATIN CAPITAL LETTER D WITH CEDILLA]
        // Ḓ  [LATIN CAPITAL LETTER D WITH CIRCUMFLEX BELOW]
        // Ⓓ  [CIRCLED LATIN CAPITAL LETTER D]
        // Ꝺ  [LATIN CAPITAL LETTER INSULAR D]
        // Ｄ  [FULLWIDTH LATIN CAPITAL LETTER D]
        case '\u00D0' | '\u010E' | '\u0110' | '\u0189' | '\u018A' | '\u018B' | '\u1D05' | '\u1D06' | '\u1E0A' | '\u1E0C' | '\u1E0E' | '\u1E10' | '\u1E12' | '\u24B9' | '\uA779' | '\uFF24' => 'D'

        // ASCII: E

        // È  [LATIN CAPITAL LETTER E WITH GRAVE]
        // É  [LATIN CAPITAL LETTER E WITH ACUTE]
        // Ê  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX]
        // Ë  [LATIN CAPITAL LETTER E WITH DIAERESIS]
        // Ē  [LATIN CAPITAL LETTER E WITH MACRON]
        // Ĕ  [LATIN CAPITAL LETTER E WITH BREVE]
        // Ė  [LATIN CAPITAL LETTER E WITH DOT ABOVE]
        // Ę  [LATIN CAPITAL LETTER E WITH OGONEK]
        // Ě  [LATIN CAPITAL LETTER E WITH CARON]
        // Ǝ  [LATIN CAPITAL LETTER REVERSED E]
        // Ɛ  [LATIN CAPITAL LETTER OPEN E]
        // Ȅ  [LATIN CAPITAL LETTER E WITH DOUBLE GRAVE]
        // Ȇ  [LATIN CAPITAL LETTER E WITH INVERTED BREVE]
        // Ȩ  [LATIN CAPITAL LETTER E WITH CEDILLA]
        // Ɇ  [LATIN CAPITAL LETTER E WITH STROKE]
        // ᴇ  [LATIN LETTER SMALL CAPITAL E]
        // Ḕ  [LATIN CAPITAL LETTER E WITH MACRON AND GRAVE]
        // Ḗ  [LATIN CAPITAL LETTER E WITH MACRON AND ACUTE]
        // Ḙ  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX BELOW]
        // Ḛ  [LATIN CAPITAL LETTER E WITH TILDE BELOW]
        // Ḝ  [LATIN CAPITAL LETTER E WITH CEDILLA AND BREVE]
        // Ẹ  [LATIN CAPITAL LETTER E WITH DOT BELOW]
        // Ẻ  [LATIN CAPITAL LETTER E WITH HOOK ABOVE]
        // Ẽ  [LATIN CAPITAL LETTER E WITH TILDE]
        // Ế  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND ACUTE]
        // Ề  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND GRAVE]
        // Ể  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE]
        // Ễ  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND TILDE]
        // Ệ  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND DOT BELOW]
        // Ⓔ  [CIRCLED LATIN CAPITAL LETTER E]
        // ⱻ  [LATIN LETTER SMALL CAPITAL TURNED E]
        // Ｅ  [FULLWIDTH LATIN CAPITAL LETTER E]
        case '\u00C8' | '\u00C9' | '\u00CA' | '\u00CB' | '\u0112' | '\u0114' | '\u0116' | '\u0118' | '\u011A' | '\u018E' | '\u0190' | '\u0204' | '\u0206' | '\u0228' | '\u0246' | '\u1D07' | '\u1E14' | '\u1E16' | '\u1E18' | '\u1E1A' | '\u1E1C' | '\u1EB8' | '\u1EBA' | '\u1EBC' | '\u1EBE' | '\u1EC0' | '\u1EC2' | '\u1EC4' | '\u1EC6' | '\u24BA' | '\u2C7B' | '\uFF25' => 'E'

        // ASCII: F

        // Ƒ  [LATIN CAPITAL LETTER F WITH HOOK]
        // Ḟ  [LATIN CAPITAL LETTER F WITH DOT ABOVE]
        // Ⓕ  [CIRCLED LATIN CAPITAL LETTER F]
        // ꜰ  [LATIN LETTER SMALL CAPITAL F]
        // Ꝼ  [LATIN CAPITAL LETTER INSULAR F]
        // ꟻ  [LATIN EPIGRAPHIC LETTER REVERSED F]
        // Ｆ  [FULLWIDTH LATIN CAPITAL LETTER F]
        case '\u0191' | '\u1E1E' | '\u24BB' | '\uA730' | '\uA77B' | '\uA7FB' | '\uFF26' => 'F'

        // ASCII: G

        // Ĝ  [LATIN CAPITAL LETTER G WITH CIRCUMFLEX]
        // Ğ  [LATIN CAPITAL LETTER G WITH BREVE]
        // Ġ  [LATIN CAPITAL LETTER G WITH DOT ABOVE]
        // Ģ  [LATIN CAPITAL LETTER G WITH CEDILLA]
        // Ɠ  [LATIN CAPITAL LETTER G WITH HOOK]
        // Ǥ  [LATIN CAPITAL LETTER G WITH STROKE]
        // ǥ  [LATIN SMALL LETTER G WITH STROKE]
        // Ǧ  [LATIN CAPITAL LETTER G WITH CARON]
        // ǧ  [LATIN SMALL LETTER G WITH CARON]
        // Ǵ  [LATIN CAPITAL LETTER G WITH ACUTE]
        // ɢ  [LATIN LETTER SMALL CAPITAL G]
        // ʛ  [LATIN LETTER SMALL CAPITAL G WITH HOOK]
        // Ḡ  [LATIN CAPITAL LETTER G WITH MACRON]
        // Ⓖ  [CIRCLED LATIN CAPITAL LETTER G]
        // Ᵹ  [LATIN CAPITAL LETTER INSULAR G]
        // Ꝿ  [LATIN CAPITAL LETTER TURNED INSULAR G]
        // Ｇ  [FULLWIDTH LATIN CAPITAL LETTER G]
        case '\u011C' | '\u011E' | '\u0120' | '\u0122' | '\u0193' | '\u01E4' | '\u01E5' | '\u01E6' | '\u01E7' | '\u01F4' | '\u0262' | '\u029B' | '\u1E20' | '\u24BC' | '\uA77D' | '\uA77E' | '\uFF27' => 'G'

        // ASCII: H

        // Ĥ  [LATIN CAPITAL LETTER H WITH CIRCUMFLEX]
        // Ħ  [LATIN CAPITAL LETTER H WITH STROKE]
        // Ȟ  [LATIN CAPITAL LETTER H WITH CARON]
        // ʜ  [LATIN LETTER SMALL CAPITAL H]
        // Ḣ  [LATIN CAPITAL LETTER H WITH DOT ABOVE]
        // Ḥ  [LATIN CAPITAL LETTER H WITH DOT BELOW]
        // Ḧ  [LATIN CAPITAL LETTER H WITH DIAERESIS]
        // Ḩ  [LATIN CAPITAL LETTER H WITH CEDILLA]
        // Ḫ  [LATIN CAPITAL LETTER H WITH BREVE BELOW]
        // Ⓗ  [CIRCLED LATIN CAPITAL LETTER H]
        // Ⱨ  [LATIN CAPITAL LETTER H WITH DESCENDER]
        // Ⱶ  [LATIN CAPITAL LETTER HALF H]
        // Ｈ  [FULLWIDTH LATIN CAPITAL LETTER H]
        case '\u0124' | '\u0126' | '\u021E' | '\u029C' | '\u1E22' | '\u1E24' | '\u1E26' | '\u1E28' | '\u1E2A' | '\u24BD' | '\u2C67' | '\u2C75' | '\uFF28' => 'H'

        // ASCII: I

        // Ì  [LATIN CAPITAL LETTER I WITH GRAVE]
        // Í  [LATIN CAPITAL LETTER I WITH ACUTE]
        // Î  [LATIN CAPITAL LETTER I WITH CIRCUMFLEX]
        // Ï  [LATIN CAPITAL LETTER I WITH DIAERESIS]
        // Ĩ  [LATIN CAPITAL LETTER I WITH TILDE]
        // Ī  [LATIN CAPITAL LETTER I WITH MACRON]
        // Ĭ  [LATIN CAPITAL LETTER I WITH BREVE]
        // Į  [LATIN CAPITAL LETTER I WITH OGONEK]
        // İ  [LATIN CAPITAL LETTER I WITH DOT ABOVE]
        // Ɩ  [LATIN CAPITAL LETTER IOTA]
        // Ɨ  [LATIN CAPITAL LETTER I WITH STROKE]
        // Ǐ  [LATIN CAPITAL LETTER I WITH CARON]
        // Ȉ  [LATIN CAPITAL LETTER I WITH DOUBLE GRAVE]
        // Ȋ  [LATIN CAPITAL LETTER I WITH INVERTED BREVE]
        // ɪ  [LATIN LETTER SMALL CAPITAL I]
        // ᵻ  [LATIN SMALL CAPITAL LETTER I WITH STROKE]
        // Ḭ  [LATIN CAPITAL LETTER I WITH TILDE BELOW]
        // Ḯ  [LATIN CAPITAL LETTER I WITH DIAERESIS AND ACUTE]
        // Ỉ  [LATIN CAPITAL LETTER I WITH HOOK ABOVE]
        // Ị  [LATIN CAPITAL LETTER I WITH DOT BELOW]
        // Ⓘ  [CIRCLED LATIN CAPITAL LETTER I]
        // ꟾ  [LATIN EPIGRAPHIC LETTER I LONGA]
        // Ｉ  [FULLWIDTH LATIN CAPITAL LETTER I]
        case '\u00CC' | '\u00CD' | '\u00CE' | '\u00CF' | '\u0128' | '\u012A' | '\u012C' | '\u012E' | '\u0130' | '\u0196' | '\u0197' | '\u01CF' | '\u0208' | '\u020A' | '\u026A' | '\u1D7B' | '\u1E2C' | '\u1E2E' | '\u1EC8' | '\u1ECA' | '\u24BE' | '\uA7FE' | '\uFF29' => 'I'

        // ASCII: J

        // Ĵ  [LATIN CAPITAL LETTER J WITH CIRCUMFLEX]
        // Ɉ  [LATIN CAPITAL LETTER J WITH STROKE]
        // ᴊ  [LATIN LETTER SMALL CAPITAL J]
        // Ⓙ  [CIRCLED LATIN CAPITAL LETTER J]
        // Ｊ  [FULLWIDTH LATIN CAPITAL LETTER J]
        case '\u0134' | '\u0248' | '\u1D0A' | '\u24BF' | '\uFF2A' => 'J'

        // ASCII: K

        // Ķ  [LATIN CAPITAL LETTER K WITH CEDILLA]
        // Ƙ  [LATIN CAPITAL LETTER K WITH HOOK]
        // Ǩ  [LATIN CAPITAL LETTER K WITH CARON]
        // ᴋ  [LATIN LETTER SMALL CAPITAL K]
        // Ḱ  [LATIN CAPITAL LETTER K WITH ACUTE]
        // Ḳ  [LATIN CAPITAL LETTER K WITH DOT BELOW]
        // Ḵ  [LATIN CAPITAL LETTER K WITH LINE BELOW]
        // Ⓚ  [CIRCLED LATIN CAPITAL LETTER K]
        // Ⱪ  [LATIN CAPITAL LETTER K WITH DESCENDER]
        // Ꝁ  [LATIN CAPITAL LETTER K WITH STROKE]
        // Ꝃ  [LATIN CAPITAL LETTER K WITH DIAGONAL STROKE]
        // Ꝅ  [LATIN CAPITAL LETTER K WITH STROKE AND DIAGONAL STROKE]
        // Ｋ  [FULLWIDTH LATIN CAPITAL LETTER K]
        case '\u0136' | '\u0198' | '\u01E8' | '\u1D0B' | '\u1E30' | '\u1E32' | '\u1E34' | '\u24C0' | '\u2C69' | '\uA740' | '\uA742' | '\uA744' | '\uFF2B' => 'K'

        // ASCII: L

        // Ĺ  [LATIN CAPITAL LETTER L WITH ACUTE]
        // Ļ  [LATIN CAPITAL LETTER L WITH CEDILLA]
        // Ľ  [LATIN CAPITAL LETTER L WITH CARON]
        // Ŀ  [LATIN CAPITAL LETTER L WITH MIDDLE DOT]
        // Ł  [LATIN CAPITAL LETTER L WITH STROKE]
        // Ƚ  [LATIN CAPITAL LETTER L WITH BAR]
        // ʟ  [LATIN LETTER SMALL CAPITAL L]
        // ᴌ  [LATIN LETTER SMALL CAPITAL L WITH STROKE]
        // Ḷ  [LATIN CAPITAL LETTER L WITH DOT BELOW]
        // Ḹ  [LATIN CAPITAL LETTER L WITH DOT BELOW AND MACRON]
        // Ḻ  [LATIN CAPITAL LETTER L WITH LINE BELOW]
        // Ḽ  [LATIN CAPITAL LETTER L WITH CIRCUMFLEX BELOW]
        // Ⓛ  [CIRCLED LATIN CAPITAL LETTER L]
        // Ⱡ  [LATIN CAPITAL LETTER L WITH DOUBLE BAR]
        // Ɫ  [LATIN CAPITAL LETTER L WITH MIDDLE TILDE]
        // Ꝇ  [LATIN CAPITAL LETTER BROKEN L]
        // Ꝉ  [LATIN CAPITAL LETTER L WITH HIGH STROKE]
        // Ꞁ  [LATIN CAPITAL LETTER TURNED L]
        // Ｌ  [FULLWIDTH LATIN CAPITAL LETTER L]
        case '\u0139' | '\u013B' | '\u013D' | '\u013F' | '\u0141' | '\u023D' | '\u029F' | '\u1D0C' | '\u1E36' | '\u1E38' | '\u1E3A' | '\u1E3C' | '\u24C1' | '\u2C60' | '\u2C62' | '\uA746' | '\uA748' | '\uA780' | '\uFF2C' => 'L'

        // ASCII: M

        // Ɯ  [LATIN CAPITAL LETTER TURNED M]
        // ᴍ  [LATIN LETTER SMALL CAPITAL M]
        // Ḿ  [LATIN CAPITAL LETTER M WITH ACUTE]
        // Ṁ  [LATIN CAPITAL LETTER M WITH DOT ABOVE]
        // Ṃ  [LATIN CAPITAL LETTER M WITH DOT BELOW]
        // Ⓜ  [CIRCLED LATIN CAPITAL LETTER M]
        // Ɱ  [LATIN CAPITAL LETTER M WITH HOOK]
        // ꟽ  [LATIN EPIGRAPHIC LETTER INVERTED M]
        // ꟿ  [LATIN EPIGRAPHIC LETTER ARCHAIC M]
        // Ｍ  [FULLWIDTH LATIN CAPITAL LETTER M]
        case '\u019C' | '\u1D0D' | '\u1E3E' | '\u1E40' | '\u1E42' | '\u24C2' | '\u2C6E' | '\uA7FD' | '\uA7FF' | '\uFF2D' => 'M'

        // ASCII: N

        // Ñ  [LATIN CAPITAL LETTER N WITH TILDE]
        // Ń  [LATIN CAPITAL LETTER N WITH ACUTE]
        // Ņ  [LATIN CAPITAL LETTER N WITH CEDILLA]
        // Ň  [LATIN CAPITAL LETTER N WITH CARON]
        // Ŋ  http://en.wikipedia.org/wiki/Eng_(letter)  [LATIN CAPITAL LETTER ENG]
        // Ɲ  [LATIN CAPITAL LETTER N WITH LEFT HOOK]
        // Ǹ  [LATIN CAPITAL LETTER N WITH GRAVE]
        // Ƞ  [LATIN CAPITAL LETTER N WITH LONG RIGHT LEG]
        // ɴ  [LATIN LETTER SMALL CAPITAL N]
        // ᴎ  [LATIN LETTER SMALL CAPITAL REVERSED N]
        // Ṅ  [LATIN CAPITAL LETTER N WITH DOT ABOVE]
        // Ṇ  [LATIN CAPITAL LETTER N WITH DOT BELOW]
        // Ṉ  [LATIN CAPITAL LETTER N WITH LINE BELOW]
        // Ṋ  [LATIN CAPITAL LETTER N WITH CIRCUMFLEX BELOW]
        // Ⓝ  [CIRCLED LATIN CAPITAL LETTER N]
        // Ｎ  [FULLWIDTH LATIN CAPITAL LETTER N]
        case '\u00D1' | '\u0143' | '\u0145' | '\u0147' | '\u014A' | '\u019D' | '\u01F8' | '\u0220' | '\u0274' | '\u1D0E' | '\u1E44' | '\u1E46' | '\u1E48' | '\u1E4A' | '\u24C3' | '\uFF2E' => 'N'

        // ASCII: O

        // Ꜵ  [LATIN CAPITAL LETTER AO]
        // Ò  [LATIN CAPITAL LETTER O WITH GRAVE]
        // Ó  [LATIN CAPITAL LETTER O WITH ACUTE]
        // Ô  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX]
        // Õ  [LATIN CAPITAL LETTER O WITH TILDE]
        // Ö  [LATIN CAPITAL LETTER O WITH DIAERESIS]
        // Ø  [LATIN CAPITAL LETTER O WITH STROKE]
        // Ō  [LATIN CAPITAL LETTER O WITH MACRON]
        // Ŏ  [LATIN CAPITAL LETTER O WITH BREVE]
        // Ő  [LATIN CAPITAL LETTER O WITH DOUBLE ACUTE]
        // Ɔ  [LATIN CAPITAL LETTER OPEN O]
        // Ɵ  [LATIN CAPITAL LETTER O WITH MIDDLE TILDE]
        // Ơ  [LATIN CAPITAL LETTER O WITH HORN]
        // Ǒ  [LATIN CAPITAL LETTER O WITH CARON]
        // Ǫ  [LATIN CAPITAL LETTER O WITH OGONEK]
        // Ǭ  [LATIN CAPITAL LETTER O WITH OGONEK AND MACRON]
        // Ǿ  [LATIN CAPITAL LETTER O WITH STROKE AND ACUTE]
        // Ȍ  [LATIN CAPITAL LETTER O WITH DOUBLE GRAVE]
        // Ȏ  [LATIN CAPITAL LETTER O WITH INVERTED BREVE]
        // Ȫ  [LATIN CAPITAL LETTER O WITH DIAERESIS AND MACRON]
        // Ȭ  [LATIN CAPITAL LETTER O WITH TILDE AND MACRON]
        // Ȯ  [LATIN CAPITAL LETTER O WITH DOT ABOVE]
        // Ȱ  [LATIN CAPITAL LETTER O WITH DOT ABOVE AND MACRON]
        // ᴏ  [LATIN LETTER SMALL CAPITAL O]
        // ᴐ  [LATIN LETTER SMALL CAPITAL OPEN O]
        // Ṍ  [LATIN CAPITAL LETTER O WITH TILDE AND ACUTE]
        // Ṏ  [LATIN CAPITAL LETTER O WITH TILDE AND DIAERESIS]
        // Ṑ  [LATIN CAPITAL LETTER O WITH MACRON AND GRAVE]
        // Ṓ  [LATIN CAPITAL LETTER O WITH MACRON AND ACUTE]
        // Ọ  [LATIN CAPITAL LETTER O WITH DOT BELOW]
        // Ỏ  [LATIN CAPITAL LETTER O WITH HOOK ABOVE]
        // Ố  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND ACUTE]
        // Ồ  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND GRAVE]
        // Ổ  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE]
        // Ỗ  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND TILDE]
        // Ộ  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND DOT BELOW]
        // Ớ  [LATIN CAPITAL LETTER O WITH HORN AND ACUTE]
        // Ờ  [LATIN CAPITAL LETTER O WITH HORN AND GRAVE]
        // Ở  [LATIN CAPITAL LETTER O WITH HORN AND HOOK ABOVE]
        // Ỡ  [LATIN CAPITAL LETTER O WITH HORN AND TILDE]
        // Ợ  [LATIN CAPITAL LETTER O WITH HORN AND DOT BELOW]
        // Ⓞ  [CIRCLED LATIN CAPITAL LETTER O]
        // Ꝋ  [LATIN CAPITAL LETTER O WITH LONG STROKE OVERLAY]
        // Ꝍ  [LATIN CAPITAL LETTER O WITH LOOP]
        // Ｏ  [FULLWIDTH LATIN CAPITAL LETTER O]
        case '\uA734' | '\u00D2' | '\u00D3' | '\u00D4' | '\u00D5' | '\u00D6' | '\u00D8' | '\u014C' | '\u014E' | '\u0150' | '\u0186' | '\u019F' | '\u01A0' | '\u01D1' | '\u01EA' | '\u01EC' | '\u01FE' | '\u020C' | '\u020E' | '\u022A' | '\u022C' | '\u022E' | '\u0230' | '\u1D0F' | '\u1D10' | '\u1E4C' | '\u1E4E' | '\u1E50' | '\u1E52' | '\u1ECC' | '\u1ECE' | '\u1ED0' | '\u1ED2' | '\u1ED4' | '\u1ED6' | '\u1ED8' | '\u1EDA' | '\u1EDC' | '\u1EDE' | '\u1EE0' | '\u1EE2' | '\u24C4' | '\uA74A' | '\uA74C' | '\uFF2F' => 'O'

        // ASCII: P

        // Ƥ  [LATIN CAPITAL LETTER P WITH HOOK]
        // ᴘ  [LATIN LETTER SMALL CAPITAL P]
        // Ṕ  [LATIN CAPITAL LETTER P WITH ACUTE]
        // Ṗ  [LATIN CAPITAL LETTER P WITH DOT ABOVE]
        // Ⓟ  [CIRCLED LATIN CAPITAL LETTER P]
        // Ᵽ  [LATIN CAPITAL LETTER P WITH STROKE]
        // Ꝑ  [LATIN CAPITAL LETTER P WITH STROKE THROUGH DESCENDER]
        // Ꝓ  [LATIN CAPITAL LETTER P WITH FLOURISH]
        // Ꝕ  [LATIN CAPITAL LETTER P WITH SQUIRREL TAIL]
        // Ｐ  [FULLWIDTH LATIN CAPITAL LETTER P]
        case '\u01A4' | '\u1D18' | '\u1E54' | '\u1E56' | '\u24C5' | '\u2C63' | '\uA750' | '\uA752' | '\uA754' | '\uFF30' => 'P'

        // ASCII: Q

        // Ɋ  [LATIN CAPITAL LETTER SMALL Q WITH HOOK TAIL]
        // Ⓠ  [CIRCLED LATIN CAPITAL LETTER Q]
        // Ꝗ  [LATIN CAPITAL LETTER Q WITH STROKE THROUGH DESCENDER]
        // Ꝙ  [LATIN CAPITAL LETTER Q WITH DIAGONAL STROKE]
        // Ｑ  [FULLWIDTH LATIN CAPITAL LETTER Q]
        case '\u024A' | '\u24C6' | '\uA756' | '\uA758' | '\uFF31' => 'Q'

        // ASCII: R

        // Ŕ  [LATIN CAPITAL LETTER R WITH ACUTE]
        // Ŗ  [LATIN CAPITAL LETTER R WITH CEDILLA]
        // Ř  [LATIN CAPITAL LETTER R WITH CARON]
        // Ȓ  [LATIN CAPITAL LETTER R WITH DOUBLE GRAVE]
        // Ȓ  [LATIN CAPITAL LETTER R WITH INVERTED BREVE]
        // Ɍ  [LATIN CAPITAL LETTER R WITH STROKE]
        // ʀ  [LATIN LETTER SMALL CAPITAL R]
        // ʁ  [LATIN LETTER SMALL CAPITAL INVERTED R]
        // ᴙ  [LATIN LETTER SMALL CAPITAL REVERSED R]
        // ᴚ  [LATIN LETTER SMALL CAPITAL TURNED R]
        // Ṙ  [LATIN CAPITAL LETTER R WITH DOT ABOVE]
        // Ṛ  [LATIN CAPITAL LETTER R WITH DOT BELOW]
        // Ṝ  [LATIN CAPITAL LETTER R WITH DOT BELOW AND MACRON]
        // Ṟ  [LATIN CAPITAL LETTER R WITH LINE BELOW]
        // Ⓡ  [CIRCLED LATIN CAPITAL LETTER R]
        // Ɽ  [LATIN CAPITAL LETTER R WITH TAIL]
        // Ꝛ  [LATIN CAPITAL LETTER R ROTUNDA]
        // Ꞃ  [LATIN CAPITAL LETTER INSULAR R]
        // Ｒ  [FULLWIDTH LATIN CAPITAL LETTER R]
        case '\u0154' | '\u0156' | '\u0158' | '\u0210' | '\u0212' | '\u024C' | '\u0280' | '\u0281' | '\u1D19' | '\u1D1A' | '\u1E58' | '\u1E5A' | '\u1E5C' | '\u1E5E' | '\u24C7' | '\u2C64' | '\uA75A' | '\uA782' | '\uFF32' => 'R'

        // ASCII: S

        // Ś  [LATIN CAPITAL LETTER S WITH ACUTE]
        // Ŝ  [LATIN CAPITAL LETTER S WITH CIRCUMFLEX]
        // Ş  [LATIN CAPITAL LETTER S WITH CEDILLA]
        // Š  [LATIN CAPITAL LETTER S WITH CARON]
        // Ș  [LATIN CAPITAL LETTER S WITH COMMA BELOW]
        // Ṡ  [LATIN CAPITAL LETTER S WITH DOT ABOVE]
        // Ṣ  [LATIN CAPITAL LETTER S WITH DOT BELOW]
        // Ṥ  [LATIN CAPITAL LETTER S WITH ACUTE AND DOT ABOVE]
        // Ṧ  [LATIN CAPITAL LETTER S WITH CARON AND DOT ABOVE]
        // Ṩ  [LATIN CAPITAL LETTER S WITH DOT BELOW AND DOT ABOVE]
        // Ⓢ  [CIRCLED LATIN CAPITAL LETTER S]
        // ꜱ  [LATIN LETTER SMALL CAPITAL S]
        // ꞅ  [LATIN SMALL LETTER INSULAR S]
        // Ｓ  [FULLWIDTH LATIN CAPITAL LETTER S]
        case '\u015A' | '\u015C' | '\u015E' | '\u0160' | '\u0218' | '\u1E60' | '\u1E62' | '\u1E64' | '\u1E66' | '\u1E68' | '\u24C8' | '\uA731' | '\uA785' | '\uFF33' => 'S'

        // ASCII: T

        // Ţ  [LATIN CAPITAL LETTER T WITH CEDILLA]
        // Ť  [LATIN CAPITAL LETTER T WITH CARON]
        // Ŧ  [LATIN CAPITAL LETTER T WITH STROKE]
        // Ƭ  [LATIN CAPITAL LETTER T WITH HOOK]
        // Ʈ  [LATIN CAPITAL LETTER T WITH RETROFLEX HOOK]
        // Ț  [LATIN CAPITAL LETTER T WITH COMMA BELOW]
        // Ⱦ  [LATIN CAPITAL LETTER T WITH DIAGONAL STROKE]
        // ᴛ  [LATIN LETTER SMALL CAPITAL T]
        // Ṫ  [LATIN CAPITAL LETTER T WITH DOT ABOVE]
        // Ṭ  [LATIN CAPITAL LETTER T WITH DOT BELOW]
        // Ṯ  [LATIN CAPITAL LETTER T WITH LINE BELOW]
        // Ṱ  [LATIN CAPITAL LETTER T WITH CIRCUMFLEX BELOW]
        // Ⓣ  [CIRCLED LATIN CAPITAL LETTER T]
        // Ꞇ  [LATIN CAPITAL LETTER INSULAR T]
        // Ｔ  [FULLWIDTH LATIN CAPITAL LETTER T]
        case '\u0162' | '\u0164' | '\u0166' | '\u01AC' | '\u01AE' | '\u021A' | '\u023E' | '\u1D1B' | '\u1E6A' | '\u1E6C' | '\u1E6E' | '\u1E70' | '\u24C9' | '\uA786' | '\uFF34' => 'T'

        // ASCII: U

        // Ù  [LATIN CAPITAL LETTER U WITH GRAVE]
        // Ú  [LATIN CAPITAL LETTER U WITH ACUTE]
        // Û  [LATIN CAPITAL LETTER U WITH CIRCUMFLEX]
        // Ü  [LATIN CAPITAL LETTER U WITH DIAERESIS]
        // Ũ  [LATIN CAPITAL LETTER U WITH TILDE]
        // Ū  [LATIN CAPITAL LETTER U WITH MACRON]
        // Ŭ  [LATIN CAPITAL LETTER U WITH BREVE]
        // Ů  [LATIN CAPITAL LETTER U WITH RING ABOVE]
        // Ű  [LATIN CAPITAL LETTER U WITH DOUBLE ACUTE]
        // Ų  [LATIN CAPITAL LETTER U WITH OGONEK]
        // Ư  [LATIN CAPITAL LETTER U WITH HORN]
        // Ǔ  [LATIN CAPITAL LETTER U WITH CARON]
        // Ǖ  [LATIN CAPITAL LETTER U WITH DIAERESIS AND MACRON]
        // Ǘ  [LATIN CAPITAL LETTER U WITH DIAERESIS AND ACUTE]
        // Ǚ  [LATIN CAPITAL LETTER U WITH DIAERESIS AND CARON]
        // Ǜ  [LATIN CAPITAL LETTER U WITH DIAERESIS AND GRAVE]
        // Ȕ  [LATIN CAPITAL LETTER U WITH DOUBLE GRAVE]
        // Ȗ  [LATIN CAPITAL LETTER U WITH INVERTED BREVE]
        // Ʉ  [LATIN CAPITAL LETTER U BAR]
        // ᴜ  [LATIN LETTER SMALL CAPITAL U]
        // ᵾ  [LATIN SMALL CAPITAL LETTER U WITH STROKE]
        // Ṳ  [LATIN CAPITAL LETTER U WITH DIAERESIS BELOW]
        // Ṵ  [LATIN CAPITAL LETTER U WITH TILDE BELOW]
        // Ṷ  [LATIN CAPITAL LETTER U WITH CIRCUMFLEX BELOW]
        // Ṹ  [LATIN CAPITAL LETTER U WITH TILDE AND ACUTE]
        // Ṻ  [LATIN CAPITAL LETTER U WITH MACRON AND DIAERESIS]
        // Ụ  [LATIN CAPITAL LETTER U WITH DOT BELOW]
        // Ủ  [LATIN CAPITAL LETTER U WITH HOOK ABOVE]
        // Ứ  [LATIN CAPITAL LETTER U WITH HORN AND ACUTE]
        // Ừ  [LATIN CAPITAL LETTER U WITH HORN AND GRAVE]
        // Ử  [LATIN CAPITAL LETTER U WITH HORN AND HOOK ABOVE]
        // Ữ  [LATIN CAPITAL LETTER U WITH HORN AND TILDE]
        // Ự  [LATIN CAPITAL LETTER U WITH HORN AND DOT BELOW]
        // Ⓤ  [CIRCLED LATIN CAPITAL LETTER U]
        // Ｕ  [FULLWIDTH LATIN CAPITAL LETTER U]
        case '\u00D9' | '\u00DA' | '\u00DB' | '\u00DC' | '\u0168' | '\u016A' | '\u016C' | '\u016E' | '\u0170' | '\u0172' | '\u01AF' | '\u01D3' | '\u01D5' | '\u01D7' | '\u01D9' | '\u01DB' | '\u0214' | '\u0216' | '\u0244' | '\u1D1C' | '\u1D7E' | '\u1E72' | '\u1E74' | '\u1E76' | '\u1E78' | '\u1E7A' | '\u1EE4' | '\u1EE6' | '\u1EE8' | '\u1EEA' | '\u1EEC' | '\u1EEE' | '\u1EF0' | '\u24CA' | '\uFF35' => 'U'

        // ASCII: V

        // Ʋ  [LATIN CAPITAL LETTER V WITH HOOK]
        // Ʌ  [LATIN CAPITAL LETTER TURNED V]
        // ᴠ  [LATIN LETTER SMALL CAPITAL V]
        // Ṽ  [LATIN CAPITAL LETTER V WITH TILDE]
        // Ṿ  [LATIN CAPITAL LETTER V WITH DOT BELOW]
        // Ỽ  [LATIN CAPITAL LETTER MIDDLE-WELSH V]
        // Ⓥ  [CIRCLED LATIN CAPITAL LETTER V]
        // Ꝟ  [LATIN CAPITAL LETTER V WITH DIAGONAL STROKE]
        // Ꝩ  [LATIN CAPITAL LETTER VEND]
        // Ｖ  [FULLWIDTH LATIN CAPITAL LETTER V]
        case '\u01B2' | '\u0245' | '\u1D20' | '\u1E7C' | '\u1E7E' | '\u1EFC' | '\u24CB' | '\uA75E' | '\uA768' | '\uFF36' => 'V'

        // ASCII: W

        // Ŵ  [LATIN CAPITAL LETTER W WITH CIRCUMFLEX]
        // Ƿ  http://en.wikipedia.org/wiki/Wynn  [LATIN CAPITAL LETTER WYNN]
        // ᴡ  [LATIN LETTER SMALL CAPITAL W]
        // Ẁ  [LATIN CAPITAL LETTER W WITH GRAVE]
        // Ẃ  [LATIN CAPITAL LETTER W WITH ACUTE]
        // Ẅ  [LATIN CAPITAL LETTER W WITH DIAERESIS]
        // Ẇ  [LATIN CAPITAL LETTER W WITH DOT ABOVE]
        // Ẉ  [LATIN CAPITAL LETTER W WITH DOT BELOW]
        // Ⓦ  [CIRCLED LATIN CAPITAL LETTER W]
        // Ⱳ  [LATIN CAPITAL LETTER W WITH HOOK]
        // Ｗ  [FULLWIDTH LATIN CAPITAL LETTER W]
        case '\u0174' | '\u01F7' | '\u1D21' | '\u1E80' | '\u1E82' | '\u1E84' | '\u1E86' | '\u1E88' | '\u24CC' | '\u2C72' | '\uFF37' => 'W'

        // ASCII: X

        // Ẋ  [LATIN CAPITAL LETTER X WITH DOT ABOVE]
        // Ẍ  [LATIN CAPITAL LETTER X WITH DIAERESIS]
        // Ⓧ  [CIRCLED LATIN CAPITAL LETTER X]
        // Ｘ  [FULLWIDTH LATIN CAPITAL LETTER X]
        case '\u1E8A' | '\u1E8C' | '\u24CD' | '\uFF38' => 'X'

        // ASCII: Y

        // Ý  [LATIN CAPITAL LETTER Y WITH ACUTE]
        // Ŷ  [LATIN CAPITAL LETTER Y WITH CIRCUMFLEX]
        // Ÿ  [LATIN CAPITAL LETTER Y WITH DIAERESIS]
        // Ƴ  [LATIN CAPITAL LETTER Y WITH HOOK]
        // Ȳ  [LATIN CAPITAL LETTER Y WITH MACRON]
        // Ɏ  [LATIN CAPITAL LETTER Y WITH STROKE]
        // ʏ  [LATIN LETTER SMALL CAPITAL Y]
        // Ẏ  [LATIN CAPITAL LETTER Y WITH DOT ABOVE]
        // Ỳ  [LATIN CAPITAL LETTER Y WITH GRAVE]
        // Ỵ  [LATIN CAPITAL LETTER Y WITH DOT BELOW]
        // Ỷ  [LATIN CAPITAL LETTER Y WITH HOOK ABOVE]
        // Ỹ  [LATIN CAPITAL LETTER Y WITH TILDE]
        // Ỿ  [LATIN CAPITAL LETTER Y WITH LOOP]
        // Ⓨ  [CIRCLED LATIN CAPITAL LETTER Y]
        // Ｙ  [FULLWIDTH LATIN CAPITAL LETTER Y]
        case '\u00DD' | '\u0176' | '\u0178' | '\u01B3' | '\u0232' | '\u024E' | '\u028F' | '\u1E8E' | '\u1EF2' | '\u1EF4' | '\u1EF6' | '\u1EF8' | '\u1EFE' | '\u24CE' | '\uFF39' => 'Y'

        // ASCII: Z

        // Ź  [LATIN CAPITAL LETTER Z WITH ACUTE]
        // Ż  [LATIN CAPITAL LETTER Z WITH DOT ABOVE]
        // Ž  [LATIN CAPITAL LETTER Z WITH CARON]
        // Ƶ  [LATIN CAPITAL LETTER Z WITH STROKE]
        // Ȝ  http://en.wikipedia.org/wiki/Yogh  [LATIN CAPITAL LETTER YOGH]
        // Ȥ  [LATIN CAPITAL LETTER Z WITH HOOK]
        // ᴢ  [LATIN LETTER SMALL CAPITAL Z]
        // Ẑ  [LATIN CAPITAL LETTER Z WITH CIRCUMFLEX]
        // Ẓ  [LATIN CAPITAL LETTER Z WITH DOT BELOW]
        // Ẕ  [LATIN CAPITAL LETTER Z WITH LINE BELOW]
        // Ⓩ  [CIRCLED LATIN CAPITAL LETTER Z]
        // Ⱬ  [LATIN CAPITAL LETTER Z WITH DESCENDER]
        // Ꝣ  [LATIN CAPITAL LETTER VISIGOTHIC Z]
        // Ｚ  [FULLWIDTH LATIN CAPITAL LETTER Z]
        case '\u0179' | '\u017B' | '\u017D' | '\u01B5' | '\u021C' | '\u0224' | '\u1D22' | '\u1E90' | '\u1E92' | '\u1E94' | '\u24CF' | '\u2C6B' | '\uA762' | '\uFF3A' => 'Z'

        // ASCII: [

        // ⁅  [LEFT SQUARE BRACKET WITH QUILL]
        // ❲  [LIGHT LEFT TORTOISE SHELL BRACKET ORNAMENT]
        // ［  [FULLWIDTH LEFT SQUARE BRACKET]
        case '\u2045' | '\u2772' | '\uFF3B' => '['

        // ASCII: ]

        // ⁆  [RIGHT SQUARE BRACKET WITH QUILL]
        // ❳  [LIGHT RIGHT TORTOISE SHELL BRACKET ORNAMENT]
        // ］  [FULLWIDTH RIGHT SQUARE BRACKET]
        case '\u2046' | '\u2773' | '\uFF3D' => ']'

        // ASCII: ^

        // ＼  [FULLWIDTH REVERSE SOLIDUS]
        // ‸  [CARET]
        // ＾  [FULLWIDTH CIRCUMFLEX ACCENT]
        case '\uFF3C' | '\u2038' | '\uFF3E' => '^'

        // ASCII: _

        // ＿  [FULLWIDTH LOW LINE]
        case '\uFF3F' => '_'

        // ASCII: a

        // à  [LATIN SMALL LETTER A WITH GRAVE]
        // á  [LATIN SMALL LETTER A WITH ACUTE]
        // â  [LATIN SMALL LETTER A WITH CIRCUMFLEX]
        // ã  [LATIN SMALL LETTER A WITH TILDE]
        // ä  [LATIN SMALL LETTER A WITH DIAERESIS]
        // å  [LATIN SMALL LETTER A WITH RING ABOVE]
        // ā  [LATIN SMALL LETTER A WITH MACRON]
        // ă  [LATIN SMALL LETTER A WITH BREVE]
        // ą  [LATIN SMALL LETTER A WITH OGONEK]
        // ǎ  [LATIN SMALL LETTER A WITH CARON]
        // ǟ  [LATIN SMALL LETTER A WITH DIAERESIS AND MACRON]
        // ǡ  [LATIN SMALL LETTER A WITH DOT ABOVE AND MACRON]
        // ǻ  [LATIN SMALL LETTER A WITH RING ABOVE AND ACUTE]
        // ȁ  [LATIN SMALL LETTER A WITH DOUBLE GRAVE]
        // ȃ  [LATIN SMALL LETTER A WITH INVERTED BREVE]
        // ȧ  [LATIN SMALL LETTER A WITH DOT ABOVE]
        // ɐ  [LATIN SMALL LETTER TURNED A]
        // ə  [LATIN SMALL LETTER SCHWA]
        // ɚ  [LATIN SMALL LETTER SCHWA WITH HOOK]
        // ᶏ  [LATIN SMALL LETTER A WITH RETROFLEX HOOK]
        // ᶕ  [LATIN SMALL LETTER SCHWA WITH RETROFLEX HOOK]
        // ạ  [LATIN SMALL LETTER A WITH RING BELOW]
        // ả  [LATIN SMALL LETTER A WITH RIGHT HALF RING]
        // ạ  [LATIN SMALL LETTER A WITH DOT BELOW]
        // ả  [LATIN SMALL LETTER A WITH HOOK ABOVE]
        // ấ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND ACUTE]
        // ầ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND GRAVE]
        // ẩ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE]
        // ẫ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND TILDE]
        // ậ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND DOT BELOW]
        // ắ  [LATIN SMALL LETTER A WITH BREVE AND ACUTE]
        // ằ  [LATIN SMALL LETTER A WITH BREVE AND GRAVE]
        // ẳ  [LATIN SMALL LETTER A WITH BREVE AND HOOK ABOVE]
        // ẵ  [LATIN SMALL LETTER A WITH BREVE AND TILDE]
        // ặ  [LATIN SMALL LETTER A WITH BREVE AND DOT BELOW]
        // ₐ  [LATIN SUBSCRIPT SMALL LETTER A]
        // ₔ  [LATIN SUBSCRIPT SMALL LETTER SCHWA]
        // ⓐ  [CIRCLED LATIN SMALL LETTER A]
        // ⱥ  [LATIN SMALL LETTER A WITH STROKE]
        // Ɐ  [LATIN CAPITAL LETTER TURNED A]
        // ａ  [FULLWIDTH LATIN SMALL LETTER A]
        case '\u00E0' | '\u00E1' | '\u00E2' | '\u00E3' | '\u00E4' | '\u00E5' | '\u0101' | '\u0103' | '\u0105' | '\u01CE' | '\u01DF' | '\u01E1' | '\u01FB' | '\u0201' | '\u0203' | '\u0227' | '\u0250' | '\u0259' | '\u025A' | '\u1D8F' | '\u1D95' | '\u1E01' | '\u1E9A' | '\u1EA1' | '\u1EA3' | '\u1EA5' | '\u1EA7' | '\u1EA9' | '\u1EAB' | '\u1EAD' | '\u1EAF' | '\u1EB1' | '\u1EB3' | '\u1EB5' | '\u1EB7' | '\u2090' | '\u2094' | '\u24D0' | '\u2C65' | '\u2C6F' | '\uFF41' => 'a'

        // ASCII: b

        // ƀ  [LATIN SMALL LETTER B WITH STROKE]
        // ƃ  [LATIN SMALL LETTER B WITH TOPBAR]
        // ɓ  [LATIN SMALL LETTER B WITH HOOK]
        // ᵬ  [LATIN SMALL LETTER B WITH MIDDLE TILDE]
        // ᶀ  [LATIN SMALL LETTER B WITH PALATAL HOOK]
        // ḃ  [LATIN SMALL LETTER B WITH DOT ABOVE]
        // ḅ  [LATIN SMALL LETTER B WITH DOT BELOW]
        // ḇ  [LATIN SMALL LETTER B WITH LINE BELOW]
        // ⓑ  [CIRCLED LATIN SMALL LETTER B]
        // ｂ  [FULLWIDTH LATIN SMALL LETTER B]
        case '\u0180' | '\u0183' | '\u0253' | '\u1D6C' | '\u1D80' | '\u1E03' | '\u1E05' | '\u1E07' | '\u24D1' | '\uFF42' => 'b'

        // ASCII: c

        // ç  [LATIN SMALL LETTER C WITH CEDILLA]
        // ć  [LATIN SMALL LETTER C WITH ACUTE]
        // ĉ  [LATIN SMALL LETTER C WITH CIRCUMFLEX]
        // ċ  [LATIN SMALL LETTER C WITH DOT ABOVE]
        // č  [LATIN SMALL LETTER C WITH CARON]
        // ƈ  [LATIN SMALL LETTER C WITH HOOK]
        // ȼ  [LATIN SMALL LETTER C WITH STROKE]
        // ɕ  [LATIN SMALL LETTER C WITH CURL]
        // ḉ  [LATIN SMALL LETTER C WITH CEDILLA AND ACUTE]
        // ↄ  [LATIN SMALL LETTER REVERSED C]
        // ⓒ  [CIRCLED LATIN SMALL LETTER C]
        // Ꜿ  [LATIN CAPITAL LETTER REVERSED C WITH DOT]
        // ꜿ  [LATIN SMALL LETTER REVERSED C WITH DOT]
        // ｃ  [FULLWIDTH LATIN SMALL LETTER C]
        case '\u00E7' | '\u0107' | '\u0109' | '\u010B' | '\u010D' | '\u0188' | '\u023C' | '\u0255' | '\u1E09' | '\u2184' | '\u24D2' | '\uA73E' | '\uA73F' | '\uFF43' => 'c'

        // ASCII: d

        // ð  [LATIN SMALL LETTER ETH]
        // ď  [LATIN SMALL LETTER D WITH CARON]
        // đ  [LATIN SMALL LETTER D WITH STROKE]
        // ƌ  [LATIN SMALL LETTER D WITH TOPBAR]
        // ȡ  [LATIN SMALL LETTER D WITH CURL]
        // ɖ  [LATIN SMALL LETTER D WITH TAIL]
        // ɗ  [LATIN SMALL LETTER D WITH HOOK]
        // ᵭ  [LATIN SMALL LETTER D WITH MIDDLE TILDE]
        // ᶁ  [LATIN SMALL LETTER D WITH PALATAL HOOK]
        // ᶑ  [LATIN SMALL LETTER D WITH HOOK AND TAIL]
        // ḋ  [LATIN SMALL LETTER D WITH DOT ABOVE]
        // ḍ  [LATIN SMALL LETTER D WITH DOT BELOW]
        // ḏ  [LATIN SMALL LETTER D WITH LINE BELOW]
        // ḑ  [LATIN SMALL LETTER D WITH CEDILLA]
        // ḓ  [LATIN SMALL LETTER D WITH CIRCUMFLEX BELOW]
        // ⓓ  [CIRCLED LATIN SMALL LETTER D]
        // ꝺ  [LATIN SMALL LETTER INSULAR D]
        // ｄ  [FULLWIDTH LATIN SMALL LETTER D]
        case '\u00F0' | '\u010F' | '\u0111' | '\u018C' | '\u0221' | '\u0256' | '\u0257' | '\u1D6D' | '\u1D81' | '\u1D91' | '\u1E0B' | '\u1E0D' | '\u1E0F' | '\u1E11' | '\u1E13' | '\u24D3' | '\uA77A' | '\uFF44' => 'd'

        // ASCII: e

        // è  [LATIN SMALL LETTER E WITH GRAVE]
        // é  [LATIN SMALL LETTER E WITH ACUTE]
        // ê  [LATIN SMALL LETTER E WITH CIRCUMFLEX]
        // ë  [LATIN SMALL LETTER E WITH DIAERESIS]
        // ē  [LATIN SMALL LETTER E WITH MACRON]
        // ĕ  [LATIN SMALL LETTER E WITH BREVE]
        // ė  [LATIN SMALL LETTER E WITH DOT ABOVE]
        // ę  [LATIN SMALL LETTER E WITH OGONEK]
        // ě  [LATIN SMALL LETTER E WITH CARON]
        // ǝ  [LATIN SMALL LETTER TURNED E]
        // ȅ  [LATIN SMALL LETTER E WITH DOUBLE GRAVE]
        // ȇ  [LATIN SMALL LETTER E WITH INVERTED BREVE]
        // ȩ  [LATIN SMALL LETTER E WITH CEDILLA]
        // ɇ  [LATIN SMALL LETTER E WITH STROKE]
        // ɘ  [LATIN SMALL LETTER REVERSED E]
        // ɛ  [LATIN SMALL LETTER OPEN E]
        // ɜ  [LATIN SMALL LETTER REVERSED OPEN E]
        // ɝ  [LATIN SMALL LETTER REVERSED OPEN E WITH HOOK]
        // ɞ  [LATIN SMALL LETTER CLOSED REVERSED OPEN E]
        // ʚ  [LATIN SMALL LETTER CLOSED OPEN E]
        // ᴈ  [LATIN SMALL LETTER TURNED OPEN E]
        // ᶒ  [LATIN SMALL LETTER E WITH RETROFLEX HOOK]
        // ᶓ  [LATIN SMALL LETTER OPEN E WITH RETROFLEX HOOK]
        // ᶔ  [LATIN SMALL LETTER REVERSED OPEN E WITH RETROFLEX HOOK]
        // ḕ  [LATIN SMALL LETTER E WITH MACRON AND GRAVE]
        // ḗ  [LATIN SMALL LETTER E WITH MACRON AND ACUTE]
        // ḙ  [LATIN SMALL LETTER E WITH CIRCUMFLEX BELOW]
        // ḛ  [LATIN SMALL LETTER E WITH TILDE BELOW]
        // ḝ  [LATIN SMALL LETTER E WITH CEDILLA AND BREVE]
        // ẹ  [LATIN SMALL LETTER E WITH DOT BELOW]
        // ẻ  [LATIN SMALL LETTER E WITH HOOK ABOVE]
        // ẽ  [LATIN SMALL LETTER E WITH TILDE]
        // ế  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND ACUTE]
        // ề  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND GRAVE]
        // ể  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE]
        // ễ  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND TILDE]
        // ệ  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND DOT BELOW]
        // ₑ  [LATIN SUBSCRIPT SMALL LETTER E]
        // ⓔ  [CIRCLED LATIN SMALL LETTER E]
        // ⱸ  [LATIN SMALL LETTER E WITH NOTCH]
        // ｅ  [FULLWIDTH LATIN SMALL LETTER E]
        case '\u00E8' | '\u00E9' | '\u00EA' | '\u00EB' | '\u0113' | '\u0115' | '\u0117' | '\u0119' | '\u011B' | '\u01DD' | '\u0205' | '\u0207' | '\u0229' | '\u0247' | '\u0258' | '\u025B' | '\u025C' | '\u025D' | '\u025E' | '\u029A' | '\u1D08' | '\u1D92' | '\u1D93' | '\u1D94' | '\u1E15' | '\u1E17' | '\u1E19' | '\u1E1B' | '\u1E1D' | '\u1EB9' | '\u1EBB' | '\u1EBD' | '\u1EBF' | '\u1EC1' | '\u1EC3' | '\u1EC5' | '\u1EC7' | '\u2091' | '\u24D4' | '\u2C78' | '\uFF45' => 'e'

        // ASCII: f

        // ƒ  [LATIN SMALL LETTER F WITH HOOK]
        // ᵮ  [LATIN SMALL LETTER F WITH MIDDLE TILDE]
        // ᶂ  [LATIN SMALL LETTER F WITH PALATAL HOOK]
        // ḟ  [LATIN SMALL LETTER F WITH DOT ABOVE]
        // ẛ  [LATIN SMALL LETTER LONG S WITH DOT ABOVE]
        // ⓕ  [CIRCLED LATIN SMALL LETTER F]
        // ꝼ  [LATIN SMALL LETTER INSULAR F]
        // ｆ  [FULLWIDTH LATIN SMALL LETTER F]
        case '\u0192' | '\u1D6E' | '\u1D82' | '\u1E1F' | '\u1E9B' | '\u24D5' | '\uA77C' | '\uFF46' => 'f'

        // ASCII: g

        // ĝ  [LATIN SMALL LETTER G WITH CIRCUMFLEX]
        // ğ  [LATIN SMALL LETTER G WITH BREVE]
        // ġ  [LATIN SMALL LETTER G WITH DOT ABOVE]
        // ģ  [LATIN SMALL LETTER G WITH CEDILLA]
        // ǵ  [LATIN SMALL LETTER G WITH ACUTE]
        // ɠ  [LATIN SMALL LETTER G WITH HOOK]
        // ɡ  [LATIN SMALL LETTER SCRIPT G]
        // ᵷ  [LATIN SMALL LETTER TURNED G]
        // ᵹ  [LATIN SMALL LETTER INSULAR G]
        // ᶃ  [LATIN SMALL LETTER G WITH PALATAL HOOK]
        // ḡ  [LATIN SMALL LETTER G WITH MACRON]
        // ⓖ  [CIRCLED LATIN SMALL LETTER G]
        // ꝿ  [LATIN SMALL LETTER TURNED INSULAR G]
        // ｇ  [FULLWIDTH LATIN SMALL LETTER G]
        case '\u011D' | '\u011F' | '\u0121' | '\u0123' | '\u01F5' | '\u0260' | '\u0261' | '\u1D77' | '\u1D79' | '\u1D83' | '\u1E21' | '\u24D6' | '\uA77F' | '\uFF47' => 'g'

        // ASCII: h

        // ĥ  [LATIN SMALL LETTER H WITH CIRCUMFLEX]
        // ħ  [LATIN SMALL LETTER H WITH STROKE]
        // ȟ  [LATIN SMALL LETTER H WITH CARON]
        // ɥ  [LATIN SMALL LETTER TURNED H]
        // ɦ  [LATIN SMALL LETTER H WITH HOOK]
        // ʮ  [LATIN SMALL LETTER TURNED H WITH FISHHOOK]
        // ʯ  [LATIN SMALL LETTER TURNED H WITH FISHHOOK AND TAIL]
        // ḣ  [LATIN SMALL LETTER H WITH DOT ABOVE]
        // ḥ  [LATIN SMALL LETTER H WITH DOT BELOW]
        // ḧ  [LATIN SMALL LETTER H WITH DIAERESIS]
        // ḩ  [LATIN SMALL LETTER H WITH CEDILLA]
        // ḫ  [LATIN SMALL LETTER H WITH BREVE BELOW]
        // ẖ  [LATIN SMALL LETTER H WITH LINE BELOW]
        // ⓗ  [CIRCLED LATIN SMALL LETTER H]
        // ⱨ  [LATIN SMALL LETTER H WITH DESCENDER]
        // ⱶ  [LATIN SMALL LETTER HALF H]
        // ｈ  [FULLWIDTH LATIN SMALL LETTER H]
        case '\u0125' | '\u0127' | '\u021F' | '\u0265' | '\u0266' | '\u02AE' | '\u02AF' | '\u1E23' | '\u1E25' | '\u1E27' | '\u1E29' | '\u1E2B' | '\u1E96' | '\u24D7' | '\u2C68' | '\u2C76' | '\uFF48' => 'h'

        // ASCII: i

        // ì  [LATIN SMALL LETTER I WITH GRAVE]
        // í  [LATIN SMALL LETTER I WITH ACUTE]
        // î  [LATIN SMALL LETTER I WITH CIRCUMFLEX]
        // ï  [LATIN SMALL LETTER I WITH DIAERESIS]
        // ĩ  [LATIN SMALL LETTER I WITH TILDE]
        // ī  [LATIN SMALL LETTER I WITH MACRON]
        // ĭ  [LATIN SMALL LETTER I WITH BREVE]
        // į  [LATIN SMALL LETTER I WITH OGONEK]
        // ı  [LATIN SMALL LETTER DOTLESS I]
        // ǐ  [LATIN SMALL LETTER I WITH CARON]
        // ȉ  [LATIN SMALL LETTER I WITH DOUBLE GRAVE]
        // ȋ  [LATIN SMALL LETTER I WITH INVERTED BREVE]
        // ɨ  [LATIN SMALL LETTER I WITH STROKE]
        // ᴉ  [LATIN SMALL LETTER TURNED I]
        // ᵢ  [LATIN SUBSCRIPT SMALL LETTER I]
        // ᵼ  [LATIN SMALL LETTER IOTA WITH STROKE]
        // ᶖ  [LATIN SMALL LETTER I WITH RETROFLEX HOOK]
        // ḭ  [LATIN SMALL LETTER I WITH TILDE BELOW]
        // ḯ  [LATIN SMALL LETTER I WITH DIAERESIS AND ACUTE]
        // ỉ  [LATIN SMALL LETTER I WITH HOOK ABOVE]
        // ị  [LATIN SMALL LETTER I WITH DOT BELOW]
        // ⁱ  [SUPERSCRIPT LATIN SMALL LETTER I]
        // ⓘ  [CIRCLED LATIN SMALL LETTER I]
        // ｉ  [FULLWIDTH LATIN SMALL LETTER I]
        case '\u00EC' | '\u00ED' | '\u00EE' | '\u00EF' | '\u0129' | '\u012B' | '\u012D' | '\u012F' | '\u0131' | '\u01D0' | '\u0209' | '\u020B' | '\u0268' | '\u1D09' | '\u1D62' | '\u1D7C' | '\u1D96' | '\u1E2D' | '\u1E2F' | '\u1EC9' | '\u1ECB' | '\u2071' | '\u24D8' | '\uFF49' => 'i'

        // ASCII: j

        // ĵ  [LATIN SMALL LETTER J WITH CIRCUMFLEX]
        // ǰ  [LATIN SMALL LETTER J WITH CARON]
        // ȷ  [LATIN SMALL LETTER DOTLESS J]
        // ɉ  [LATIN SMALL LETTER J WITH STROKE]
        // ɟ  [LATIN SMALL LETTER DOTLESS J WITH STROKE]
        // ʄ  [LATIN SMALL LETTER DOTLESS J WITH STROKE AND HOOK]
        // ʝ  [LATIN SMALL LETTER J WITH CROSSED-TAIL]
        // ⓙ  [CIRCLED LATIN SMALL LETTER J]
        // ⱼ  [LATIN SUBSCRIPT SMALL LETTER J]
        // ｊ  [FULLWIDTH LATIN SMALL LETTER J]
        case '\u0135' | '\u01F0' | '\u0237' | '\u0249' | '\u025F' | '\u0284' | '\u029D' | '\u24D9' | '\u2C7C' | '\uFF4A' => 'j'

        // ASCII: k

        // ķ  [LATIN SMALL LETTER K WITH CEDILLA]
        // ƙ  [LATIN SMALL LETTER K WITH HOOK]
        // ǩ  [LATIN SMALL LETTER K WITH CARON]
        // ʞ  [LATIN SMALL LETTER TURNED K]
        // ᶄ  [LATIN SMALL LETTER K WITH PALATAL HOOK]
        // ḱ  [LATIN SMALL LETTER K WITH ACUTE]
        // ḳ  [LATIN SMALL LETTER K WITH DOT BELOW]
        // ḵ  [LATIN SMALL LETTER K WITH LINE BELOW]
        // ⓚ  [CIRCLED LATIN SMALL LETTER K]
        // ⱪ  [LATIN SMALL LETTER K WITH DESCENDER]
        // ꝁ  [LATIN SMALL LETTER K WITH STROKE]
        // ꝃ  [LATIN SMALL LETTER K WITH DIAGONAL STROKE]
        // ꝅ  [LATIN SMALL LETTER K WITH STROKE AND DIAGONAL STROKE]
        // ｋ  [FULLWIDTH LATIN SMALL LETTER K]
        case '\u0137' | '\u0199' | '\u01E9' | '\u029E' | '\u1D84' | '\u1E31' | '\u1E33' | '\u1E35' | '\u24DA' | '\u2C6A' | '\uA741' | '\uA743' | '\uA745' | '\uFF4B' => 'k'

        // ASCII: l

        // ĺ  [LATIN SMALL LETTER L WITH ACUTE]
        // ļ  [LATIN SMALL LETTER L WITH CEDILLA]
        // ľ  [LATIN SMALL LETTER L WITH CARON]
        // ŀ  [LATIN SMALL LETTER L WITH MIDDLE DOT]
        // ł  [LATIN SMALL LETTER L WITH STROKE]
        // ƚ  [LATIN SMALL LETTER L WITH BAR]
        // ȴ  [LATIN SMALL LETTER L WITH CURL]
        // ɫ  [LATIN SMALL LETTER L WITH MIDDLE TILDE]
        // ɬ  [LATIN SMALL LETTER L WITH BELT]
        // ɭ  [LATIN SMALL LETTER L WITH RETROFLEX HOOK]
        // ᶅ  [LATIN SMALL LETTER L WITH PALATAL HOOK]
        // ḷ  [LATIN SMALL LETTER L WITH DOT BELOW]
        // ḹ  [LATIN SMALL LETTER L WITH DOT BELOW AND MACRON]
        // ḻ  [LATIN SMALL LETTER L WITH LINE BELOW]
        // ḽ  [LATIN SMALL LETTER L WITH CIRCUMFLEX BELOW]
        // ⓛ  [CIRCLED LATIN SMALL LETTER L]
        // ⱡ  [LATIN SMALL LETTER L WITH DOUBLE BAR]
        // ꝇ  [LATIN SMALL LETTER BROKEN L]
        // ꝉ  [LATIN SMALL LETTER L WITH HIGH STROKE]
        // ꞁ  [LATIN SMALL LETTER TURNED L]
        // ｌ  [FULLWIDTH LATIN SMALL LETTER L]
        case '\u013A' | '\u013C' | '\u013E' | '\u0140' | '\u0142' | '\u019A' | '\u0234' | '\u026B' | '\u026C' | '\u026D' | '\u1D85' | '\u1E37' | '\u1E39' | '\u1E3B' | '\u1E3D' | '\u24DB' | '\u2C61' | '\uA747' | '\uA749' | '\uA781' | '\uFF4C' => 'l'

        // ASCII: m

        // ɯ  [LATIN SMALL LETTER TURNED M]
        // ɰ  [LATIN SMALL LETTER TURNED M WITH LONG LEG]
        // ɱ  [LATIN SMALL LETTER M WITH HOOK]
        // ᵯ  [LATIN SMALL LETTER M WITH MIDDLE TILDE]
        // ᶆ  [LATIN SMALL LETTER M WITH PALATAL HOOK]
        // ḿ  [LATIN SMALL LETTER M WITH ACUTE]
        // ṁ  [LATIN SMALL LETTER M WITH DOT ABOVE]
        // ṃ  [LATIN SMALL LETTER M WITH DOT BELOW]
        // ⓜ  [CIRCLED LATIN SMALL LETTER M]
        // ｍ  [FULLWIDTH LATIN SMALL LETTER M]
        case '\u026F' | '\u0270' | '\u0271' | '\u1D6F' | '\u1D86' | '\u1E3F' | '\u1E41' | '\u1E43' | '\u24DC' | '\uFF4D' => 'm'

        // ASCII: n

        // ñ  [LATIN SMALL LETTER N WITH TILDE]
        // ń  [LATIN SMALL LETTER N WITH ACUTE]
        // ņ  [LATIN SMALL LETTER N WITH CEDILLA]
        // ň  [LATIN SMALL LETTER N WITH CARON]
        // ŉ  [LATIN SMALL LETTER N PRECEDED BY APOSTROPHE]
        // ŋ  http://en.wikipedia.org/wiki/Eng_(letter)  [LATIN SMALL LETTER ENG]
        // ƞ  [LATIN SMALL LETTER N WITH LONG RIGHT LEG]
        // ǹ  [LATIN SMALL LETTER N WITH GRAVE]
        // ȵ  [LATIN SMALL LETTER N WITH CURL]
        // ɲ  [LATIN SMALL LETTER N WITH LEFT HOOK]
        // ɳ  [LATIN SMALL LETTER N WITH RETROFLEX HOOK]
        // ᵰ  [LATIN SMALL LETTER N WITH MIDDLE TILDE]
        // ᶇ  [LATIN SMALL LETTER N WITH PALATAL HOOK]
        // ṅ  [LATIN SMALL LETTER N WITH DOT ABOVE]
        // ṇ  [LATIN SMALL LETTER N WITH DOT BELOW]
        // ṉ  [LATIN SMALL LETTER N WITH LINE BELOW]
        // ṋ  [LATIN SMALL LETTER N WITH CIRCUMFLEX BELOW]
        // ⁿ  [SUPERSCRIPT LATIN SMALL LETTER N]
        // ⓝ  [CIRCLED LATIN SMALL LETTER N]
        // ｎ  [FULLWIDTH LATIN SMALL LETTER N]
        case '\u00F1' | '\u0144' | '\u0146' | '\u0148' | '\u0149' | '\u014B' | '\u019E' | '\u01F9' | '\u0235' | '\u0272' | '\u0273' | '\u1D70' | '\u1D87' | '\u1E45' | '\u1E47' | '\u1E49' | '\u1E4B' | '\u207F' | '\u24DD' | '\uFF4E' => 'n'

        // ASCII: o

        // ò  [LATIN SMALL LETTER O WITH GRAVE]
        // ó  [LATIN SMALL LETTER O WITH ACUTE]
        // ô  [LATIN SMALL LETTER O WITH CIRCUMFLEX]
        // õ  [LATIN SMALL LETTER O WITH TILDE]
        // ö  [LATIN SMALL LETTER O WITH DIAERESIS]
        // ø  [LATIN SMALL LETTER O WITH STROKE]
        // ō  [LATIN SMALL LETTER O WITH MACRON]
        // ŏ  [LATIN SMALL LETTER O WITH BREVE]
        // ő  [LATIN SMALL LETTER O WITH DOUBLE ACUTE]
        // ơ  [LATIN SMALL LETTER O WITH HORN]
        // ǒ  [LATIN SMALL LETTER O WITH CARON]
        // ǫ  [LATIN SMALL LETTER O WITH OGONEK]
        // ǭ  [LATIN SMALL LETTER O WITH OGONEK AND MACRON]
        // ǿ  [LATIN SMALL LETTER O WITH STROKE AND ACUTE]
        // ȍ  [LATIN SMALL LETTER O WITH DOUBLE GRAVE]
        // ȏ  [LATIN SMALL LETTER O WITH INVERTED BREVE]
        // ȫ  [LATIN SMALL LETTER O WITH DIAERESIS AND MACRON]
        // ȭ  [LATIN SMALL LETTER O WITH TILDE AND MACRON]
        // ȯ  [LATIN SMALL LETTER O WITH DOT ABOVE]
        // ȱ  [LATIN SMALL LETTER O WITH DOT ABOVE AND MACRON]
        // ɔ  [LATIN SMALL LETTER OPEN O]
        // ɵ  [LATIN SMALL LETTER BARRED O]
        // ᴖ  [LATIN SMALL LETTER TOP HALF O]
        // ᴗ  [LATIN SMALL LETTER BOTTOM HALF O]
        // ᶗ  [LATIN SMALL LETTER OPEN O WITH RETROFLEX HOOK]
        // ṍ  [LATIN SMALL LETTER O WITH TILDE AND ACUTE]
        // ṏ  [LATIN SMALL LETTER O WITH TILDE AND DIAERESIS]
        // ṑ  [LATIN SMALL LETTER O WITH MACRON AND GRAVE]
        // ṓ  [LATIN SMALL LETTER O WITH MACRON AND ACUTE]
        // ọ  [LATIN SMALL LETTER O WITH DOT BELOW]
        // ỏ  [LATIN SMALL LETTER O WITH HOOK ABOVE]
        // ố  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND ACUTE]
        // ồ  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND GRAVE]
        // ổ  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE]
        // ỗ  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND TILDE]
        // ộ  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND DOT BELOW]
        // ớ  [LATIN SMALL LETTER O WITH HORN AND ACUTE]
        // ờ  [LATIN SMALL LETTER O WITH HORN AND GRAVE]
        // ở  [LATIN SMALL LETTER O WITH HORN AND HOOK ABOVE]
        // ỡ  [LATIN SMALL LETTER O WITH HORN AND TILDE]
        // ợ  [LATIN SMALL LETTER O WITH HORN AND DOT BELOW]
        // ₒ  [LATIN SUBSCRIPT SMALL LETTER O]
        // ⓞ  [CIRCLED LATIN SMALL LETTER O]
        // ⱺ  [LATIN SMALL LETTER O WITH LOW RING INSIDE]
        // ꝋ  [LATIN SMALL LETTER O WITH LONG STROKE OVERLAY]
        // ꝍ  [LATIN SMALL LETTER O WITH LOOP]
        // ｏ  [FULLWIDTH LATIN SMALL LETTER O]
        case '\u00F2' | '\u00F3' | '\u00F4' | '\u00F5' | '\u00F6' | '\u00F8' | '\u014D' | '\u014F' | '\u0151' | '\u01A1' | '\u01D2' | '\u01EB' | '\u01ED' | '\u01FF' | '\u020D' | '\u020F' | '\u022B' | '\u022D' | '\u022F' | '\u0231' | '\u0254' | '\u0275' | '\u1D16' | '\u1D17' | '\u1D97' | '\u1E4D' | '\u1E4F' | '\u1E51' | '\u1E53' | '\u1ECD' | '\u1ECF' | '\u1ED1' | '\u1ED3' | '\u1ED5' | '\u1ED7' | '\u1ED9' | '\u1EDB' | '\u1EDD' | '\u1EDF' | '\u1EE1' | '\u1EE3' | '\u2092' | '\u24DE' | '\u2C7A' | '\uA74B' | '\uA74D' | '\uFF4F' => 'o'

        // ASCII: p

        // ƥ  [LATIN SMALL LETTER P WITH HOOK]
        // ᵱ  [LATIN SMALL LETTER P WITH MIDDLE TILDE]
        // ᵽ  [LATIN SMALL LETTER P WITH STROKE]
        // ᶈ  [LATIN SMALL LETTER P WITH PALATAL HOOK]
        // ṕ  [LATIN SMALL LETTER P WITH ACUTE]
        // ṗ  [LATIN SMALL LETTER P WITH DOT ABOVE]
        // ⓟ  [CIRCLED LATIN SMALL LETTER P]
        // ꝑ  [LATIN SMALL LETTER P WITH STROKE THROUGH DESCENDER]
        // ꝓ  [LATIN SMALL LETTER P WITH FLOURISH]
        // ꝕ  [LATIN SMALL LETTER P WITH SQUIRREL TAIL]
        // ꟼ  [LATIN EPIGRAPHIC LETTER REVERSED P]
        // ｐ  [FULLWIDTH LATIN SMALL LETTER P]
        case '\u01A5' | '\u1D71' | '\u1D7D' | '\u1D88' | '\u1E55' | '\u1E57' | '\u24DF' | '\uA751' | '\uA753' | '\uA755' | '\uA7FC' | '\uFF50' => 'p'

        // ASCII: q

        // ĸ  http://en.wikipedia.org/wiki/Kra_(letter)  [LATIN SMALL LETTER KRA]
        // ɋ  [LATIN SMALL LETTER Q WITH HOOK TAIL]
        // ʠ  [LATIN SMALL LETTER Q WITH HOOK]
        // ⓠ  [CIRCLED LATIN SMALL LETTER Q]
        // ꝗ  [LATIN SMALL LETTER Q WITH STROKE THROUGH DESCENDER]
        // ꝙ  [LATIN SMALL LETTER Q WITH DIAGONAL STROKE]
        // ｑ  [FULLWIDTH LATIN SMALL LETTER Q]
        case '\u0138' | '\u024B' | '\u02A0' | '\u24E0' | '\uA757' | '\uA759' | '\uFF51' => 'q'

        // ASCII: r

        // ŕ  [LATIN SMALL LETTER R WITH ACUTE]
        // ŗ  [LATIN SMALL LETTER R WITH CEDILLA]
        // ř  [LATIN SMALL LETTER R WITH CARON]
        // ȑ  [LATIN SMALL LETTER R WITH DOUBLE GRAVE]
        // ȓ  [LATIN SMALL LETTER R WITH INVERTED BREVE]
        // ɍ  [LATIN SMALL LETTER R WITH STROKE]
        // ɼ  [LATIN SMALL LETTER R WITH LONG LEG]
        // ɽ  [LATIN SMALL LETTER R WITH TAIL]
        // ɾ  [LATIN SMALL LETTER R WITH FISHHOOK]
        // ɿ  [LATIN SMALL LETTER REVERSED R WITH FISHHOOK]
        // ᵣ  [LATIN SUBSCRIPT SMALL LETTER R]
        // ᵲ  [LATIN SMALL LETTER R WITH MIDDLE TILDE]
        // ᵳ  [LATIN SMALL LETTER R WITH FISHHOOK AND MIDDLE TILDE]
        // ᶉ  [LATIN SMALL LETTER R WITH PALATAL HOOK]
        // ṙ  [LATIN SMALL LETTER R WITH DOT ABOVE]
        // ṛ  [LATIN SMALL LETTER R WITH DOT BELOW]
        // ṝ  [LATIN SMALL LETTER R WITH DOT BELOW AND MACRON]
        // ṟ  [LATIN SMALL LETTER R WITH LINE BELOW]
        // ⓡ  [CIRCLED LATIN SMALL LETTER R]
        // ꝛ  [LATIN SMALL LETTER R ROTUNDA]
        // ꞃ  [LATIN SMALL LETTER INSULAR R]
        // ｒ  [FULLWIDTH LATIN SMALL LETTER R]
        case '\u0155' | '\u0157' | '\u0159' | '\u0211' | '\u0213' | '\u024D' | '\u027C' | '\u027D' | '\u027E' | '\u027F' | '\u1D63' | '\u1D72' | '\u1D73' | '\u1D89' | '\u1E59' | '\u1E5B' | '\u1E5D' | '\u1E5F' | '\u24E1' | '\uA75B' | '\uA783' | '\uFF52' => 'r'

        // ASCII: s

        // ś  [LATIN SMALL LETTER S WITH ACUTE]
        // ŝ  [LATIN SMALL LETTER S WITH CIRCUMFLEX]
        // ş  [LATIN SMALL LETTER S WITH CEDILLA]
        // š  [LATIN SMALL LETTER S WITH CARON]
        // ſ  http://en.wikipedia.org/wiki/Long_S  [LATIN SMALL LETTER LONG S]
        // ș  [LATIN SMALL LETTER S WITH COMMA BELOW]
        // ȿ  [LATIN SMALL LETTER S WITH SWASH TAIL]
        // ʂ  [LATIN SMALL LETTER S WITH HOOK]
        // ᵴ  [LATIN SMALL LETTER S WITH MIDDLE TILDE]
        // ᶊ  [LATIN SMALL LETTER S WITH PALATAL HOOK]
        // ṡ  [LATIN SMALL LETTER S WITH DOT ABOVE]
        // ṣ  [LATIN SMALL LETTER S WITH DOT BELOW]
        // ṥ  [LATIN SMALL LETTER S WITH ACUTE AND DOT ABOVE]
        // ṧ  [LATIN SMALL LETTER S WITH CARON AND DOT ABOVE]
        // ṩ  [LATIN SMALL LETTER S WITH DOT BELOW AND DOT ABOVE]
        // ẜ  [LATIN SMALL LETTER LONG S WITH DIAGONAL STROKE]
        // ẝ  [LATIN SMALL LETTER LONG S WITH HIGH STROKE]
        // ⓢ  [CIRCLED LATIN SMALL LETTER S]
        // Ꞅ  [LATIN CAPITAL LETTER INSULAR S]
        // ｓ  [FULLWIDTH LATIN SMALL LETTER S]
        case '\u015B' | '\u015D' | '\u015F' | '\u0161' | '\u017F' | '\u0219' | '\u023F' | '\u0282' | '\u1D74' | '\u1D8A' | '\u1E61' | '\u1E63' | '\u1E65' | '\u1E67' | '\u1E69' | '\u1E9C' | '\u1E9D' | '\u24E2' | '\uA784' | '\uFF53' => 's'

        // ASCII: t

        // ţ  [LATIN SMALL LETTER T WITH CEDILLA]
        // ť  [LATIN SMALL LETTER T WITH CARON]
        // ŧ  [LATIN SMALL LETTER T WITH STROKE]
        // ƫ  [LATIN SMALL LETTER T WITH PALATAL HOOK]
        // ƭ  [LATIN SMALL LETTER T WITH HOOK]
        // ț  [LATIN SMALL LETTER T WITH COMMA BELOW]
        // ȶ  [LATIN SMALL LETTER T WITH CURL]
        // ʇ  [LATIN SMALL LETTER TURNED T]
        // ʈ  [LATIN SMALL LETTER T WITH RETROFLEX HOOK]
        // ᵵ  [LATIN SMALL LETTER T WITH MIDDLE TILDE]
        // ṫ  [LATIN SMALL LETTER T WITH DOT ABOVE]
        // ṭ  [LATIN SMALL LETTER T WITH DOT BELOW]
        // ṯ  [LATIN SMALL LETTER T WITH LINE BELOW]
        // ṱ  [LATIN SMALL LETTER T WITH CIRCUMFLEX BELOW]
        // ẗ  [LATIN SMALL LETTER T WITH DIAERESIS]
        // ⓣ  [CIRCLED LATIN SMALL LETTER T]
        // ⱦ  [LATIN SMALL LETTER T WITH DIAGONAL STROKE]
        // ｔ  [FULLWIDTH LATIN SMALL LETTER T]
        case '\u0163' | '\u0165' | '\u0167' | '\u01AB' | '\u01AD' | '\u021B' | '\u0236' | '\u0287' | '\u0288' | '\u1D75' | '\u1E6B' | '\u1E6D' | '\u1E6F' | '\u1E71' | '\u1E97' | '\u24E3' | '\u2C66' | '\uFF54' => 't'

        // ASCII: u

        // ù  [LATIN SMALL LETTER U WITH GRAVE]
        // ú  [LATIN SMALL LETTER U WITH ACUTE]
        // û  [LATIN SMALL LETTER U WITH CIRCUMFLEX]
        // ü  [LATIN SMALL LETTER U WITH DIAERESIS]
        // ũ  [LATIN SMALL LETTER U WITH TILDE]
        // ū  [LATIN SMALL LETTER U WITH MACRON]
        // ŭ  [LATIN SMALL LETTER U WITH BREVE]
        // ů  [LATIN SMALL LETTER U WITH RING ABOVE]
        // ű  [LATIN SMALL LETTER U WITH DOUBLE ACUTE]
        // ų  [LATIN SMALL LETTER U WITH OGONEK]
        // ư  [LATIN SMALL LETTER U WITH HORN]
        // ǔ  [LATIN SMALL LETTER U WITH CARON]
        // ǖ  [LATIN SMALL LETTER U WITH DIAERESIS AND MACRON]
        // ǘ  [LATIN SMALL LETTER U WITH DIAERESIS AND ACUTE]
        // ǚ  [LATIN SMALL LETTER U WITH DIAERESIS AND CARON]
        // ǜ  [LATIN SMALL LETTER U WITH DIAERESIS AND GRAVE]
        // ȕ  [LATIN SMALL LETTER U WITH DOUBLE GRAVE]
        // ȗ  [LATIN SMALL LETTER U WITH INVERTED BREVE]
        // ʉ  [LATIN SMALL LETTER U BAR]
        // ᵤ  [LATIN SUBSCRIPT SMALL LETTER U]
        // ᶙ  [LATIN SMALL LETTER U WITH RETROFLEX HOOK]
        // ṳ  [LATIN SMALL LETTER U WITH DIAERESIS BELOW]
        // ṵ  [LATIN SMALL LETTER U WITH TILDE BELOW]
        // ṷ  [LATIN SMALL LETTER U WITH CIRCUMFLEX BELOW]
        // ṹ  [LATIN SMALL LETTER U WITH TILDE AND ACUTE]
        // ṻ  [LATIN SMALL LETTER U WITH MACRON AND DIAERESIS]
        // ụ  [LATIN SMALL LETTER U WITH DOT BELOW]
        // ủ  [LATIN SMALL LETTER U WITH HOOK ABOVE]
        // ứ  [LATIN SMALL LETTER U WITH HORN AND ACUTE]
        // ừ  [LATIN SMALL LETTER U WITH HORN AND GRAVE]
        // ử  [LATIN SMALL LETTER U WITH HORN AND HOOK ABOVE]
        // ữ  [LATIN SMALL LETTER U WITH HORN AND TILDE]
        // ự  [LATIN SMALL LETTER U WITH HORN AND DOT BELOW]
        // ⓤ  [CIRCLED LATIN SMALL LETTER U]
        // ｕ  [FULLWIDTH LATIN SMALL LETTER U]
        case '\u00F9' | '\u00FA' | '\u00FB' | '\u00FC' | '\u0169' | '\u016B' | '\u016D' | '\u016F' | '\u0171' | '\u0173' | '\u01B0' | '\u01D4' | '\u01D6' | '\u01D8' | '\u01DA' | '\u01DC' | '\u0215' | '\u0217' | '\u0289' | '\u1D64' | '\u1D99' | '\u1E73' | '\u1E75' | '\u1E77' | '\u1E79' | '\u1E7B' | '\u1EE5' | '\u1EE7' | '\u1EE9' | '\u1EEB' | '\u1EED' | '\u1EEF' | '\u1EF1' | '\u24E4' | '\uFF55' => 'u'

        // ASCII: v

        // ʋ  [LATIN SMALL LETTER V WITH HOOK]
        // ʌ  [LATIN SMALL LETTER TURNED V]
        // ᵥ  [LATIN SUBSCRIPT SMALL LETTER V]
        // ᶌ  [LATIN SMALL LETTER V WITH PALATAL HOOK]
        // ṽ  [LATIN SMALL LETTER V WITH TILDE]
        // ṿ  [LATIN SMALL LETTER V WITH DOT BELOW]
        // ⓥ  [CIRCLED LATIN SMALL LETTER V]
        // ⱱ  [LATIN SMALL LETTER V WITH RIGHT HOOK]
        // ⱴ  [LATIN SMALL LETTER V WITH CURL]
        // ꝟ  [LATIN SMALL LETTER V WITH DIAGONAL STROKE]
        // ｖ  [FULLWIDTH LATIN SMALL LETTER V]
        case '\u028B' | '\u028C' | '\u1D65' | '\u1D8C' | '\u1E7D' | '\u1E7F' | '\u24E5' | '\u2C71' | '\u2C74' | '\uA75F' | '\uFF56' => 'v'

        // ASCII: w

        // ŵ  [LATIN SMALL LETTER W WITH CIRCUMFLEX]
        // ƿ  http://en.wikipedia.org/wiki/Wynn  [LATIN LETTER WYNN]
        // ʍ  [LATIN SMALL LETTER TURNED W]
        // ẁ  [LATIN SMALL LETTER W WITH GRAVE]
        // ẃ  [LATIN SMALL LETTER W WITH ACUTE]
        // ẅ  [LATIN SMALL LETTER W WITH DIAERESIS]
        // ẇ  [LATIN SMALL LETTER W WITH DOT ABOVE]
        // ẉ  [LATIN SMALL LETTER W WITH DOT BELOW]
        // ẘ  [LATIN SMALL LETTER W WITH RING ABOVE]
        // ⓦ  [CIRCLED LATIN SMALL LETTER W]
        // ⱳ  [LATIN SMALL LETTER W WITH HOOK]
        // ｗ  [FULLWIDTH LATIN SMALL LETTER W]
        case '\u0175' | '\u01BF' | '\u028D' | '\u1E81' | '\u1E83' | '\u1E85' | '\u1E87' | '\u1E89' | '\u1E98' | '\u24E6' | '\u2C73' | '\uFF57' => 'w'

        // ASCII: x

        // ᶍ  [LATIN SMALL LETTER X WITH PALATAL HOOK]
        // ẋ  [LATIN SMALL LETTER X WITH DOT ABOVE]
        // ẍ  [LATIN SMALL LETTER X WITH DIAERESIS]
        // ₓ  [LATIN SUBSCRIPT SMALL LETTER X]
        // ⓧ  [CIRCLED LATIN SMALL LETTER X]
        // ｘ  [FULLWIDTH LATIN SMALL LETTER X]
        case '\u1D8D' | '\u1E8B' | '\u1E8D' | '\u2093' | '\u24E7' | '\uFF58' => 'x'

        // ASCII: y

        // ý  [LATIN SMALL LETTER Y WITH ACUTE]
        // ÿ  [LATIN SMALL LETTER Y WITH DIAERESIS]
        // ŷ  [LATIN SMALL LETTER Y WITH CIRCUMFLEX]
        // ƴ  [LATIN SMALL LETTER Y WITH HOOK]
        // ȳ  [LATIN SMALL LETTER Y WITH MACRON]
        // ɏ  [LATIN SMALL LETTER Y WITH STROKE]
        // ʎ  [LATIN SMALL LETTER TURNED Y]
        // ẏ  [LATIN SMALL LETTER Y WITH DOT ABOVE]
        // ẙ  [LATIN SMALL LETTER Y WITH RING ABOVE]
        // ỳ  [LATIN SMALL LETTER Y WITH GRAVE]
        // ỵ  [LATIN SMALL LETTER Y WITH DOT BELOW]
        // ỷ  [LATIN SMALL LETTER Y WITH HOOK ABOVE]
        // ỹ  [LATIN SMALL LETTER Y WITH TILDE]
        // ỿ  [LATIN SMALL LETTER Y WITH LOOP]
        // ⓨ  [CIRCLED LATIN SMALL LETTER Y]
        // ｙ  [FULLWIDTH LATIN SMALL LETTER Y]
        case '\u00FD' | '\u00FF' | '\u0177' | '\u01B4' | '\u0233' | '\u024F' | '\u028E' | '\u1E8F' | '\u1E99' | '\u1EF3' | '\u1EF5' | '\u1EF7' | '\u1EF9' | '\u1EFF' | '\u24E8' | '\uFF59' => 'y'

        // ASCII: z

        // ź  [LATIN SMALL LETTER Z WITH ACUTE]
        // ż  [LATIN SMALL LETTER Z WITH DOT ABOVE]
        // ž  [LATIN SMALL LETTER Z WITH CARON]
        // ƶ  [LATIN SMALL LETTER Z WITH STROKE]
        // ȝ  http://en.wikipedia.org/wiki/Yogh  [LATIN SMALL LETTER YOGH]
        // ȥ  [LATIN SMALL LETTER Z WITH HOOK]
        // ɀ  [LATIN SMALL LETTER Z WITH SWASH TAIL]
        // ʐ  [LATIN SMALL LETTER Z WITH RETROFLEX HOOK]
        // ʑ  [LATIN SMALL LETTER Z WITH CURL]
        // ᵶ  [LATIN SMALL LETTER Z WITH MIDDLE TILDE]
        // ᶎ  [LATIN SMALL LETTER Z WITH PALATAL HOOK]
        // ẑ  [LATIN SMALL LETTER Z WITH CIRCUMFLEX]
        // ẓ  [LATIN SMALL LETTER Z WITH DOT BELOW]
        // ẕ  [LATIN SMALL LETTER Z WITH LINE BELOW]
        // ⓩ  [CIRCLED LATIN SMALL LETTER Z]
        // ⱬ  [LATIN SMALL LETTER Z WITH DESCENDER]
        // ꝣ  [LATIN SMALL LETTER VISIGOTHIC Z]
        // ｚ  [FULLWIDTH LATIN SMALL LETTER Z]
        case '\u017A' | '\u017C' | '\u017E' | '\u01B6' | '\u021D' | '\u0225' | '\u0240' | '\u0290' | '\u0291' | '\u1D76' | '\u1D8E' | '\u1E91' | '\u1E93' | '\u1E95' | '\u24E9' | '\u2C6C' | '\uA763' | '\uFF5A' => 'z'

        // ASCII: {

        // ❴  [MEDIUM LEFT CURLY BRACKET ORNAMENT]
        // ｛  [FULLWIDTH LEFT CURLY BRACKET]
        case '\u2774' | '\uFF5B' => '{'

        // ASCII: }

        // ❵  [MEDIUM RIGHT CURLY BRACKET ORNAMENT]
        // ｝  [FULLWIDTH RIGHT CURLY BRACKET]
        case '\u2775' | '\uFF5D' => '}'

        case _ => c // Default
      }
    }
  }

  
  

  /** Generated From Lucene's ASCIIFoldingFilter.java */
  private def stripAccentStringImplOrNull(c: Char): String = {
    // Quick test: if it's not in range then just keep current character
    if (c < '\u0080') {
      c.toString
    } else {
      (c: @switch) match {

        // ASCII: !

        // ！  [FULLWIDTH EXCLAMATION MARK]
        case '\uFF01' => "!"

        // ASCII: !!

        // ‼  [DOUBLE EXCLAMATION MARK]
        case '\u203C' => "!!"

        // ASCII: !?

        // ⁉  [EXCLAMATION QUESTION MARK]
        case '\u2049' => "!?"

        // ASCII: "

        // «  [LEFT-POINTING DOUBLE ANGLE QUOTATION MARK]
        // »  [RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK]
        // “  [LEFT DOUBLE QUOTATION MARK]
        // ”  [RIGHT DOUBLE QUOTATION MARK]
        // „  [DOUBLE LOW-9 QUOTATION MARK]
        // ″  [DOUBLE PRIME]
        // ‶  [REVERSED DOUBLE PRIME]
        // ❝  [HEAVY DOUBLE TURNED COMMA QUOTATION MARK ORNAMENT]
        // ❞  [HEAVY DOUBLE COMMA QUOTATION MARK ORNAMENT]
        // ❮  [HEAVY LEFT-POINTING ANGLE QUOTATION MARK ORNAMENT]
        // ❯  [HEAVY RIGHT-POINTING ANGLE QUOTATION MARK ORNAMENT]
        // ＂  [FULLWIDTH QUOTATION MARK]
        case '\u00AB' | '\u00BB' | '\u201C' | '\u201D' | '\u201E' | '\u2033' | '\u2036' | '\u275D' | '\u275E' | '\u276E' | '\u276F' | '\uFF02' => "\""

        // ASCII: #

        // ＃  [FULLWIDTH NUMBER SIGN]
        case '\uFF03' => "#"

        // ASCII: $

        // ＄  [FULLWIDTH DOLLAR SIGN]
        case '\uFF04' => "$"

        // ASCII: %

        // ⁒  [COMMERCIAL MINUS SIGN]
        // ％  [FULLWIDTH PERCENT SIGN]
        case '\u2052' | '\uFF05' => "%"

        // ASCII: &

        // ＆  [FULLWIDTH AMPERSAND]
        case '\uFF06' => "&"

        // ASCII: (

        // ⁽  [SUPERSCRIPT LEFT PARENTHESIS]
        // ₍  [SUBSCRIPT LEFT PARENTHESIS]
        // ❨  [MEDIUM LEFT PARENTHESIS ORNAMENT]
        // ❪  [MEDIUM FLATTENED LEFT PARENTHESIS ORNAMENT]
        // （  [FULLWIDTH LEFT PARENTHESIS]
        case '\u207D' | '\u208D' | '\u2768' | '\u276A' | '\uFF08' => "("

        // ASCII: ((

        // ⸨  [LEFT DOUBLE PARENTHESIS]
        case '\u2E28' => "(("

        // ASCII: (1)

        // ⑴  [PARENTHESIZED DIGIT ONE]
        case '\u2474' => "(1)"

        // ASCII: (10)

        // ⑽  [PARENTHESIZED NUMBER TEN]
        case '\u247D' => "(10)"

        // ASCII: (11)

        // ⑾  [PARENTHESIZED NUMBER ELEVEN]
        case '\u247E' => "(11)"

        // ASCII: (12)

        // ⑿  [PARENTHESIZED NUMBER TWELVE]
        case '\u247F' => "(12)"

        // ASCII: (13)

        // ⒀  [PARENTHESIZED NUMBER THIRTEEN]
        case '\u2480' => "(13)"

        // ASCII: (14)

        // ⒁  [PARENTHESIZED NUMBER FOURTEEN]
        case '\u2481' => "(14)"

        // ASCII: (15)

        // ⒂  [PARENTHESIZED NUMBER FIFTEEN]
        case '\u2482' => "(15)"

        // ASCII: (16)

        // ⒃  [PARENTHESIZED NUMBER SIXTEEN]
        case '\u2483' => "(16)"

        // ASCII: (17)

        // ⒄  [PARENTHESIZED NUMBER SEVENTEEN]
        case '\u2484' => "(17)"

        // ASCII: (18)

        // ⒅  [PARENTHESIZED NUMBER EIGHTEEN]
        case '\u2485' => "(18)"

        // ASCII: (19)

        // ⒆  [PARENTHESIZED NUMBER NINETEEN]
        case '\u2486' => "(19)"

        // ASCII: (2)

        // ⑵  [PARENTHESIZED DIGIT TWO]
        case '\u2475' => "(2)"

        // ASCII: (20)

        // ⒇  [PARENTHESIZED NUMBER TWENTY]
        case '\u2487' => "(20)"

        // ASCII: (3)

        // ⑶  [PARENTHESIZED DIGIT THREE]
        case '\u2476' => "(3)"

        // ASCII: (4)

        // ⑷  [PARENTHESIZED DIGIT FOUR]
        case '\u2477' => "(4)"

        // ASCII: (5)

        // ⑸  [PARENTHESIZED DIGIT FIVE]
        case '\u2478' => "(5)"

        // ASCII: (6)

        // ⑹  [PARENTHESIZED DIGIT SIX]
        case '\u2479' => "(6)"

        // ASCII: (7)

        // ⑺  [PARENTHESIZED DIGIT SEVEN]
        case '\u247A' => "(7)"

        // ASCII: (8)

        // ⑻  [PARENTHESIZED DIGIT EIGHT]
        case '\u247B' => "(8)"

        // ASCII: (9)

        // ⑼  [PARENTHESIZED DIGIT NINE]
        case '\u247C' => "(9)"

        // ASCII: (a)

        // ⒜  [PARENTHESIZED LATIN SMALL LETTER A]
        case '\u249C' => "(a)"

        // ASCII: (c)

        // ⒞  [PARENTHESIZED LATIN SMALL LETTER C]
        case '\u249E' => "(c)"

        // ASCII: (d)

        // ⒟  [PARENTHESIZED LATIN SMALL LETTER D]
        case '\u249F' => "(d)"

        // ASCII: (e)

        // ⒠  [PARENTHESIZED LATIN SMALL LETTER E]
        case '\u24A0' => "(e)"

        // ASCII: (f)

        // ⒡  [PARENTHESIZED LATIN SMALL LETTER F]
        case '\u24A1' => "(f)"

        // ASCII: (g)

        // ⒢  [PARENTHESIZED LATIN SMALL LETTER G]
        case '\u24A2' => "(g)"

        // ASCII: (h)

        // ⒣  [PARENTHESIZED LATIN SMALL LETTER H]
        case '\u24A3' => "(h)"

        // ASCII: (i)

        // ⒤  [PARENTHESIZED LATIN SMALL LETTER I]
        case '\u24A4' => "(i)"

        // ASCII: (j)

        // ⒥  [PARENTHESIZED LATIN SMALL LETTER J]
        case '\u24A5' => "(j)"

        // ASCII: (k)

        // ⒦  [PARENTHESIZED LATIN SMALL LETTER K]
        case '\u24A6' => "(k)"

        // ASCII: (l)

        // ⒧  [PARENTHESIZED LATIN SMALL LETTER L]
        case '\u24A7' => "(l)"

        // ASCII: (m)

        // ⒨  [PARENTHESIZED LATIN SMALL LETTER M]
        case '\u24A8' => "(m)"

        // ASCII: (n)

        // ⒩  [PARENTHESIZED LATIN SMALL LETTER N]
        case '\u24A9' => "(n)"

        // ASCII: (o)

        // ⒪  [PARENTHESIZED LATIN SMALL LETTER O]
        case '\u24AA' => "(o)"

        // ASCII: (p)

        // ⒫  [PARENTHESIZED LATIN SMALL LETTER P]
        case '\u24AB' => "(p)"

        // ASCII: (q)

        // ⒬  [PARENTHESIZED LATIN SMALL LETTER Q]
        case '\u24AC' => "(q)"

        // ASCII: (r)

        // ⒭  [PARENTHESIZED LATIN SMALL LETTER R]
        case '\u24AD' => "(r)"

        // ASCII: (s)

        // ⒮  [PARENTHESIZED LATIN SMALL LETTER S]
        case '\u24AE' => "(s)"

        // ASCII: (t)

        // ⒯  [PARENTHESIZED LATIN SMALL LETTER T]
        case '\u24AF' => "(t)"

        // ASCII: (u)

        // ⒰  [PARENTHESIZED LATIN SMALL LETTER U]
        case '\u24B0' => "(u)"

        // ASCII: (v)

        // ⒱  [PARENTHESIZED LATIN SMALL LETTER V]
        case '\u24B1' => "(v)"

        // ASCII: (w)

        // ⒲  [PARENTHESIZED LATIN SMALL LETTER W]
        case '\u24B2' => "(w)"

        // ASCII: (x)

        // ⒳  [PARENTHESIZED LATIN SMALL LETTER X]
        case '\u24B3' => "(x)"

        // ASCII: (y)

        // ⒴  [PARENTHESIZED LATIN SMALL LETTER Y]
        case '\u24B4' => "(y)"

        // ASCII: (z)

        // ⒵  [PARENTHESIZED LATIN SMALL LETTER Z]
        case '\u24B5' => "(z)"

        // ASCII: )

        // ⁾  [SUPERSCRIPT RIGHT PARENTHESIS]
        // ₎  [SUBSCRIPT RIGHT PARENTHESIS]
        // ❩  [MEDIUM RIGHT PARENTHESIS ORNAMENT]
        // ❫  [MEDIUM FLATTENED RIGHT PARENTHESIS ORNAMENT]
        // ）  [FULLWIDTH RIGHT PARENTHESIS]
        case '\u207E' | '\u208E' | '\u2769' | '\u276B' | '\uFF09' => ")"

        // ASCII: ))

        // ⸩  [RIGHT DOUBLE PARENTHESIS]
        case '\u2E29' => "))"

        // ASCII: *

        // ⁎  [LOW ASTERISK]
        // ＊  [FULLWIDTH ASTERISK]
        case '\u204E' | '\uFF0A' => "*"

        // ASCII: +

        // ⁺  [SUPERSCRIPT PLUS SIGN]
        // ₊  [SUBSCRIPT PLUS SIGN]
        // ＋  [FULLWIDTH PLUS SIGN]
        case '\u207A' | '\u208A' | '\uFF0B' => "+"

        // ASCII: ,

        // ，  [FULLWIDTH COMMA]
        case '\uFF0C' => ","

        // ASCII: -

        // ‘  [LEFT SINGLE QUOTATION MARK]
        // ’  [RIGHT SINGLE QUOTATION MARK]
        // ‚  [SINGLE LOW-9 QUOTATION MARK]
        // ‛  [SINGLE HIGH-REVERSED-9 QUOTATION MARK]
        // ′  [PRIME]
        // ‵  [REVERSED PRIME]
        // ‹  [SINGLE LEFT-POINTING ANGLE QUOTATION MARK]
        // ›  [SINGLE RIGHT-POINTING ANGLE QUOTATION MARK]
        // ❛  [HEAVY SINGLE TURNED COMMA QUOTATION MARK ORNAMENT]
        // ❜  [HEAVY SINGLE COMMA QUOTATION MARK ORNAMENT]
        // ＇  [FULLWIDTH APOSTROPHE]
        // ‐  [HYPHEN]
        // ‑  [NON-BREAKING HYPHEN]
        // ‒  [FIGURE DASH]
        // –  [EN DASH]
        // —  [EM DASH]
        // ⁻  [SUPERSCRIPT MINUS]
        // ₋  [SUBSCRIPT MINUS]
        // －  [FULLWIDTH HYPHEN-MINUS]
        case '\u2018' | '\u2019' | '\u201A' | '\u201B' | '\u2032' | '\u2035' | '\u2039' | '\u203A' | '\u275B' | '\u275C' | '\uFF07' | '\u2010' | '\u2011' | '\u2012' | '\u2013' | '\u2014' | '\u207B' | '\u208B' | '\uFF0D' => "-"

        // ASCII: .

        // ．  [FULLWIDTH FULL STOP]
        case '\uFF0E' => "."

        // ASCII: /

        // ⁄  [FRACTION SLASH]
        // ／  [FULLWIDTH SOLIDUS]
        case '\u2044' | '\uFF0F' => "/"

        // ASCII: 0

        // ⁰  [SUPERSCRIPT ZERO]
        // ₀  [SUBSCRIPT ZERO]
        // ⓪  [CIRCLED DIGIT ZERO]
        // ⓿  [NEGATIVE CIRCLED DIGIT ZERO]
        // ０  [FULLWIDTH DIGIT ZERO]
        case '\u2070' | '\u2080' | '\u24EA' | '\u24FF' | '\uFF10' => "0"

        // ASCII: 1

        // ¹  [SUPERSCRIPT ONE]
        // ₁  [SUBSCRIPT ONE]
        // ①  [CIRCLED DIGIT ONE]
        // ⓵  [DOUBLE CIRCLED DIGIT ONE]
        // ❶  [DINGBAT NEGATIVE CIRCLED DIGIT ONE]
        // ➀  [DINGBAT CIRCLED SANS-SERIF DIGIT ONE]
        // ➊  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT ONE]
        // １  [FULLWIDTH DIGIT ONE]
        case '\u00B9' | '\u2081' | '\u2460' | '\u24F5' | '\u2776' | '\u2780' | '\u278A' | '\uFF11' => "1"

        // ASCII: 1.

        // ⒈  [DIGIT ONE FULL STOP]
        case '\u2488' => "1."

        // ASCII: 10

        // ⑩  [CIRCLED NUMBER TEN]
        // ⓾  [DOUBLE CIRCLED NUMBER TEN]
        // ❿  [DINGBAT NEGATIVE CIRCLED NUMBER TEN]
        // ➉  [DINGBAT CIRCLED SANS-SERIF NUMBER TEN]
        // ➓  [DINGBAT NEGATIVE CIRCLED SANS-SERIF NUMBER TEN]
        case '\u2469' | '\u24FE' | '\u277F' | '\u2789' | '\u2793' => "10"

        // ASCII: 10.

        // ⒑  [NUMBER TEN FULL STOP]
        case '\u2491' => "10."

        // ASCII: 11

        // ⑪  [CIRCLED NUMBER ELEVEN]
        // ⓫  [NEGATIVE CIRCLED NUMBER ELEVEN]
        case '\u246A' | '\u24EB' => "11"

        // ASCII: 11.

        // ⒒  [NUMBER ELEVEN FULL STOP]
        case '\u2492' => "11."

        // ASCII: 12

        // ⑫  [CIRCLED NUMBER TWELVE]
        // ⓬  [NEGATIVE CIRCLED NUMBER TWELVE]
        case '\u246B' | '\u24EC' => "12"

        // ASCII: 12.

        // ⒓  [NUMBER TWELVE FULL STOP]
        case '\u2493' => "12."

        // ASCII: 13

        // ⑬  [CIRCLED NUMBER THIRTEEN]
        // ⓭  [NEGATIVE CIRCLED NUMBER THIRTEEN]
        case '\u246C' | '\u24ED' => "13"

        // ASCII: 13.

        // ⒔  [NUMBER THIRTEEN FULL STOP]
        case '\u2494' => "13."

        // ASCII: 14

        // ⑭  [CIRCLED NUMBER FOURTEEN]
        // ⓮  [NEGATIVE CIRCLED NUMBER FOURTEEN]
        case '\u246D' | '\u24EE' => "14"

        // ASCII: 14.

        // ⒕  [NUMBER FOURTEEN FULL STOP]
        case '\u2495' => "14."

        // ASCII: 15

        // ⑮  [CIRCLED NUMBER FIFTEEN]
        // ⓯  [NEGATIVE CIRCLED NUMBER FIFTEEN]
        case '\u246E' | '\u24EF' => "15"

        // ASCII: 15.

        // ⒖  [NUMBER FIFTEEN FULL STOP]
        case '\u2496' => "15."

        // ASCII: 16

        // ⑯  [CIRCLED NUMBER SIXTEEN]
        // ⓰  [NEGATIVE CIRCLED NUMBER SIXTEEN]
        case '\u246F' | '\u24F0' => "16"

        // ASCII: 16.

        // ⒗  [NUMBER SIXTEEN FULL STOP]
        case '\u2497' => "16."

        // ASCII: 17

        // ⑰  [CIRCLED NUMBER SEVENTEEN]
        // ⓱  [NEGATIVE CIRCLED NUMBER SEVENTEEN]
        case '\u2470' | '\u24F1' => "17"

        // ASCII: 17.

        // ⒘  [NUMBER SEVENTEEN FULL STOP]
        case '\u2498' => "17."

        // ASCII: 18

        // ⑱  [CIRCLED NUMBER EIGHTEEN]
        // ⓲  [NEGATIVE CIRCLED NUMBER EIGHTEEN]
        case '\u2471' | '\u24F2' => "18"

        // ASCII: 18.

        // ⒙  [NUMBER EIGHTEEN FULL STOP]
        case '\u2499' => "18."

        // ASCII: 19

        // ⑲  [CIRCLED NUMBER NINETEEN]
        // ⓳  [NEGATIVE CIRCLED NUMBER NINETEEN]
        case '\u2472' | '\u24F3' => "19"

        // ASCII: 19.

        // ⒚  [NUMBER NINETEEN FULL STOP]
        case '\u249A' => "19."

        // ASCII: 2

        // ²  [SUPERSCRIPT TWO]
        // ₂  [SUBSCRIPT TWO]
        // ②  [CIRCLED DIGIT TWO]
        // ⓶  [DOUBLE CIRCLED DIGIT TWO]
        // ❷  [DINGBAT NEGATIVE CIRCLED DIGIT TWO]
        // ➁  [DINGBAT CIRCLED SANS-SERIF DIGIT TWO]
        // ➋  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT TWO]
        // ２  [FULLWIDTH DIGIT TWO]
        case '\u00B2' | '\u2082' | '\u2461' | '\u24F6' | '\u2777' | '\u2781' | '\u278B' | '\uFF12' => "2"

        // ASCII: 2.

        // ⒉  [DIGIT TWO FULL STOP]
        case '\u2489' => "2."

        // ASCII: 20

        // ⑳  [CIRCLED NUMBER TWENTY]
        // ⓴  [NEGATIVE CIRCLED NUMBER TWENTY]
        case '\u2473' | '\u24F4' => "20"

        // ASCII: 20.

        // ⒛  [NUMBER TWENTY FULL STOP]
        case '\u249B' => "20."

        // ASCII: 3

        // ³  [SUPERSCRIPT THREE]
        // ₃  [SUBSCRIPT THREE]
        // ③  [CIRCLED DIGIT THREE]
        // ⓷  [DOUBLE CIRCLED DIGIT THREE]
        // ❸  [DINGBAT NEGATIVE CIRCLED DIGIT THREE]
        // ➂  [DINGBAT CIRCLED SANS-SERIF DIGIT THREE]
        // ➌  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT THREE]
        // ３  [FULLWIDTH DIGIT THREE]
        case '\u00B3' | '\u2083' | '\u2462' | '\u24F7' | '\u2778' | '\u2782' | '\u278C' | '\uFF13' => "3"

        // ASCII: 3.

        // ⒊  [DIGIT THREE FULL STOP]
        case '\u248A' => "3."

        // ASCII: 4

        // ⁴  [SUPERSCRIPT FOUR]
        // ₄  [SUBSCRIPT FOUR]
        // ④  [CIRCLED DIGIT FOUR]
        // ⓸  [DOUBLE CIRCLED DIGIT FOUR]
        // ❹  [DINGBAT NEGATIVE CIRCLED DIGIT FOUR]
        // ➃  [DINGBAT CIRCLED SANS-SERIF DIGIT FOUR]
        // ➍  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FOUR]
        // ４  [FULLWIDTH DIGIT FOUR]
        case '\u2074' | '\u2084' | '\u2463' | '\u24F8' | '\u2779' | '\u2783' | '\u278D' | '\uFF14' => "4"

        // ASCII: 4.

        // ⒋  [DIGIT FOUR FULL STOP]
        case '\u248B' => "4."

        // ASCII: 5

        // ⁵  [SUPERSCRIPT FIVE]
        // ₅  [SUBSCRIPT FIVE]
        // ⑤  [CIRCLED DIGIT FIVE]
        // ⓹  [DOUBLE CIRCLED DIGIT FIVE]
        // ❺  [DINGBAT NEGATIVE CIRCLED DIGIT FIVE]
        // ➄  [DINGBAT CIRCLED SANS-SERIF DIGIT FIVE]
        // ➎  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FIVE]
        // ５  [FULLWIDTH DIGIT FIVE]
        case '\u2075' | '\u2085' | '\u2464' | '\u24F9' | '\u277A' | '\u2784' | '\u278E' | '\uFF15' => "5"

        // ASCII: 5.

        // ⒌  [DIGIT FIVE FULL STOP]
        case '\u248C' => "5."

        // ASCII: 6

        // ⁶  [SUPERSCRIPT SIX]
        // ₆  [SUBSCRIPT SIX]
        // ⑥  [CIRCLED DIGIT SIX]
        // ⓺  [DOUBLE CIRCLED DIGIT SIX]
        // ❻  [DINGBAT NEGATIVE CIRCLED DIGIT SIX]
        // ➅  [DINGBAT CIRCLED SANS-SERIF DIGIT SIX]
        // ➏  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SIX]
        // ６  [FULLWIDTH DIGIT SIX]
        case '\u2076' | '\u2086' | '\u2465' | '\u24FA' | '\u277B' | '\u2785' | '\u278F' | '\uFF16' => "6"

        // ASCII: 6.

        // ⒍  [DIGIT SIX FULL STOP]
        case '\u248D' => "6."

        // ASCII: 7

        // ⁷  [SUPERSCRIPT SEVEN]
        // ₇  [SUBSCRIPT SEVEN]
        // ⑦  [CIRCLED DIGIT SEVEN]
        // ⓻  [DOUBLE CIRCLED DIGIT SEVEN]
        // ❼  [DINGBAT NEGATIVE CIRCLED DIGIT SEVEN]
        // ➆  [DINGBAT CIRCLED SANS-SERIF DIGIT SEVEN]
        // ➐  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SEVEN]
        // ７  [FULLWIDTH DIGIT SEVEN]
        case '\u2077' | '\u2087' | '\u2466' | '\u24FB' | '\u277C' | '\u2786' | '\u2790' | '\uFF17' => "7"

        // ASCII: 7.

        // ⒎  [DIGIT SEVEN FULL STOP]
        case '\u248E' => "7."

        // ASCII: 8

        // ⁸  [SUPERSCRIPT EIGHT]
        // ₈  [SUBSCRIPT EIGHT]
        // ⑧  [CIRCLED DIGIT EIGHT]
        // ⓼  [DOUBLE CIRCLED DIGIT EIGHT]
        // ❽  [DINGBAT NEGATIVE CIRCLED DIGIT EIGHT]
        // ➇  [DINGBAT CIRCLED SANS-SERIF DIGIT EIGHT]
        // ➑  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT EIGHT]
        // ８  [FULLWIDTH DIGIT EIGHT]
        case '\u2078' | '\u2088' | '\u2467' | '\u24FC' | '\u277D' | '\u2787' | '\u2791' | '\uFF18' => "8"

        // ASCII: 8.

        // ⒏  [DIGIT EIGHT FULL STOP]
        case '\u248F' => "8."

        // ASCII: 9

        // ⁹  [SUPERSCRIPT NINE]
        // ₉  [SUBSCRIPT NINE]
        // ⑨  [CIRCLED DIGIT NINE]
        // ⓽  [DOUBLE CIRCLED DIGIT NINE]
        // ❾  [DINGBAT NEGATIVE CIRCLED DIGIT NINE]
        // ➈  [DINGBAT CIRCLED SANS-SERIF DIGIT NINE]
        // ➒  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT NINE]
        // ９  [FULLWIDTH DIGIT NINE]
        case '\u2079' | '\u2089' | '\u2468' | '\u24FD' | '\u277E' | '\u2788' | '\u2792' | '\uFF19' => "9"

        // ASCII: 9.

        // ⒐  [DIGIT NINE FULL STOP]
        case '\u2490' => "9."

        // ASCII: :

        // ：  [FULLWIDTH COLON]
        case '\uFF1A' => ":"

        // ASCII: ;

        // ⁏  [REVERSED SEMICOLON]
        // ；  [FULLWIDTH SEMICOLON]
        case '\u204F' | '\uFF1B' => ";"

        // ASCII: <

        // ❬  [MEDIUM LEFT-POINTING ANGLE BRACKET ORNAMENT]
        // ❰  [HEAVY LEFT-POINTING ANGLE BRACKET ORNAMENT]
        // ＜  [FULLWIDTH LESS-THAN SIGN]
        case '\u276C' | '\u2770' | '\uFF1C' => "<"

        // ASCII: =

        // ⁼  [SUPERSCRIPT EQUALS SIGN]
        // ₌  [SUBSCRIPT EQUALS SIGN]
        // ＝  [FULLWIDTH EQUALS SIGN]
        case '\u207C' | '\u208C' | '\uFF1D' => "="

        // ASCII: >

        // ❭  [MEDIUM RIGHT-POINTING ANGLE BRACKET ORNAMENT]
        // ❱  [HEAVY RIGHT-POINTING ANGLE BRACKET ORNAMENT]
        // ＞  [FULLWIDTH GREATER-THAN SIGN]
        case '\u276D' | '\u2771' | '\uFF1E' => ">"

        // ASCII: ?

        // ？  [FULLWIDTH QUESTION MARK]
        case '\uFF1F' => "?"

        // ASCII: ?!

        // ⁈  [QUESTION EXCLAMATION MARK]
        case '\u2048' => "?!"

        // ASCII: ??

        // ⁇  [DOUBLE QUESTION MARK]
        case '\u2047' => "??"

        // ASCII: @

        // ＠  [FULLWIDTH COMMERCIAL AT]
        case '\uFF20' => "@"

        // ASCII: A

        // À  [LATIN CAPITAL LETTER A WITH GRAVE]
        // Á  [LATIN CAPITAL LETTER A WITH ACUTE]
        // Â  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX]
        // Ã  [LATIN CAPITAL LETTER A WITH TILDE]
        // Ä  [LATIN CAPITAL LETTER A WITH DIAERESIS]
        // Å  [LATIN CAPITAL LETTER A WITH RING ABOVE]
        // Ā  [LATIN CAPITAL LETTER A WITH MACRON]
        // Ă  [LATIN CAPITAL LETTER A WITH BREVE]
        // Ą  [LATIN CAPITAL LETTER A WITH OGONEK]
        // Ə  http://en.wikipedia.org/wiki/Schwa  [LATIN CAPITAL LETTER SCHWA]
        // Ǎ  [LATIN CAPITAL LETTER A WITH CARON]
        // Ǟ  [LATIN CAPITAL LETTER A WITH DIAERESIS AND MACRON]
        // Ǡ  [LATIN CAPITAL LETTER A WITH DOT ABOVE AND MACRON]
        // Ǻ  [LATIN CAPITAL LETTER A WITH RING ABOVE AND ACUTE]
        // Ȁ  [LATIN CAPITAL LETTER A WITH DOUBLE GRAVE]
        // Ȃ  [LATIN CAPITAL LETTER A WITH INVERTED BREVE]
        // Ȧ  [LATIN CAPITAL LETTER A WITH DOT ABOVE]
        // Ⱥ  [LATIN CAPITAL LETTER A WITH STROKE]
        // ᴀ  [LATIN LETTER SMALL CAPITAL A]
        // Ḁ  [LATIN CAPITAL LETTER A WITH RING BELOW]
        // Ạ  [LATIN CAPITAL LETTER A WITH DOT BELOW]
        // Ả  [LATIN CAPITAL LETTER A WITH HOOK ABOVE]
        // Ấ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND ACUTE]
        // Ầ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND GRAVE]
        // Ẩ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE]
        // Ẫ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND TILDE]
        // Ậ  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND DOT BELOW]
        // Ắ  [LATIN CAPITAL LETTER A WITH BREVE AND ACUTE]
        // Ằ  [LATIN CAPITAL LETTER A WITH BREVE AND GRAVE]
        // Ẳ  [LATIN CAPITAL LETTER A WITH BREVE AND HOOK ABOVE]
        // Ẵ  [LATIN CAPITAL LETTER A WITH BREVE AND TILDE]
        // Ặ  [LATIN CAPITAL LETTER A WITH BREVE AND DOT BELOW]
        // Ⓐ  [CIRCLED LATIN CAPITAL LETTER A]
        // Ａ  [FULLWIDTH LATIN CAPITAL LETTER A]
        case '\u00C0' | '\u00C1' | '\u00C2' | '\u00C3' | '\u00C4' | '\u00C5' | '\u0100' | '\u0102' | '\u0104' | '\u018F' | '\u01CD' | '\u01DE' | '\u01E0' | '\u01FA' | '\u0200' | '\u0202' | '\u0226' | '\u023A' | '\u1D00' | '\u1E00' | '\u1EA0' | '\u1EA2' | '\u1EA4' | '\u1EA6' | '\u1EA8' | '\u1EAA' | '\u1EAC' | '\u1EAE' | '\u1EB0' | '\u1EB2' | '\u1EB4' | '\u1EB6' | '\u24B6' | '\uFF21' => "A"

        // ASCII: AA

        // Ꜳ  [LATIN CAPITAL LETTER AA]
        case '\uA732' => "AA"

        // ASCII: AE

        // Æ  [LATIN CAPITAL LETTER AE]
        // Ǣ  [LATIN CAPITAL LETTER AE WITH MACRON]
        // Ǽ  [LATIN CAPITAL LETTER AE WITH ACUTE]
        // ᴁ  [LATIN LETTER SMALL CAPITAL AE]
        case '\u00C6' | '\u01E2' | '\u01FC' | '\u1D01' => "AE"

        // ASCII: AU

        // Ꜷ  [LATIN CAPITAL LETTER AU]
        case '\uA736' => "AU"

        // ASCII: AV

        // Ꜹ  [LATIN CAPITAL LETTER AV]
        // Ꜻ  [LATIN CAPITAL LETTER AV WITH HORIZONTAL BAR]
        case '\uA738' | '\uA73A' => "AV"

        // ASCII: AY

        // Ꜽ  [LATIN CAPITAL LETTER AY]
        case '\uA73C' => "AY"

        // ASCII: B

        // Ɓ  [LATIN CAPITAL LETTER B WITH HOOK]
        // Ƃ  [LATIN CAPITAL LETTER B WITH TOPBAR]
        // Ƀ  [LATIN CAPITAL LETTER B WITH STROKE]
        // ʙ  [LATIN LETTER SMALL CAPITAL B]
        // ᴃ  [LATIN LETTER SMALL CAPITAL BARRED B]
        // Ḃ  [LATIN CAPITAL LETTER B WITH DOT ABOVE]
        // Ḅ  [LATIN CAPITAL LETTER B WITH DOT BELOW]
        // Ḇ  [LATIN CAPITAL LETTER B WITH LINE BELOW]
        // Ⓑ  [CIRCLED LATIN CAPITAL LETTER B]
        // Ｂ  [FULLWIDTH LATIN CAPITAL LETTER B]
        case '\u0181' | '\u0182' | '\u0243' | '\u0299' | '\u1D03' | '\u1E02' | '\u1E04' | '\u1E06' | '\u24B7' | '\uFF22' => "B"

        // ASCII: C

        // Ç  [LATIN CAPITAL LETTER C WITH CEDILLA]
        // Ć  [LATIN CAPITAL LETTER C WITH ACUTE]
        // Ĉ  [LATIN CAPITAL LETTER C WITH CIRCUMFLEX]
        // Ċ  [LATIN CAPITAL LETTER C WITH DOT ABOVE]
        // Č  [LATIN CAPITAL LETTER C WITH CARON]
        // Ƈ  [LATIN CAPITAL LETTER C WITH HOOK]
        // Ȼ  [LATIN CAPITAL LETTER C WITH STROKE]
        // ʗ  [LATIN LETTER STRETCHED C]
        // ᴄ  [LATIN LETTER SMALL CAPITAL C]
        // Ḉ  [LATIN CAPITAL LETTER C WITH CEDILLA AND ACUTE]
        // Ⓒ  [CIRCLED LATIN CAPITAL LETTER C]
        // Ｃ  [FULLWIDTH LATIN CAPITAL LETTER C]
        case '\u00C7' | '\u0106' | '\u0108' | '\u010A' | '\u010C' | '\u0187' | '\u023B' | '\u0297' | '\u1D04' | '\u1E08' | '\u24B8' | '\uFF23' => "C"

        // ASCII: D

        // Ð  [LATIN CAPITAL LETTER ETH]
        // Ď  [LATIN CAPITAL LETTER D WITH CARON]
        // Đ  [LATIN CAPITAL LETTER D WITH STROKE]
        // Ɖ  [LATIN CAPITAL LETTER AFRICAN D]
        // Ɗ  [LATIN CAPITAL LETTER D WITH HOOK]
        // Ƌ  [LATIN CAPITAL LETTER D WITH TOPBAR]
        // ᴅ  [LATIN LETTER SMALL CAPITAL D]
        // ᴆ  [LATIN LETTER SMALL CAPITAL ETH]
        // Ḋ  [LATIN CAPITAL LETTER D WITH DOT ABOVE]
        // Ḍ  [LATIN CAPITAL LETTER D WITH DOT BELOW]
        // Ḏ  [LATIN CAPITAL LETTER D WITH LINE BELOW]
        // Ḑ  [LATIN CAPITAL LETTER D WITH CEDILLA]
        // Ḓ  [LATIN CAPITAL LETTER D WITH CIRCUMFLEX BELOW]
        // Ⓓ  [CIRCLED LATIN CAPITAL LETTER D]
        // Ꝺ  [LATIN CAPITAL LETTER INSULAR D]
        // Ｄ  [FULLWIDTH LATIN CAPITAL LETTER D]
        case '\u00D0' | '\u010E' | '\u0110' | '\u0189' | '\u018A' | '\u018B' | '\u1D05' | '\u1D06' | '\u1E0A' | '\u1E0C' | '\u1E0E' | '\u1E10' | '\u1E12' | '\u24B9' | '\uA779' | '\uFF24' => "D"

        // ASCII: DZ

        // Ǆ  [LATIN CAPITAL LETTER DZ WITH CARON]
        // Ǳ  [LATIN CAPITAL LETTER DZ]
        case '\u01C4' | '\u01F1' => "DZ"

        // ASCII: Dz

        // ǅ  [LATIN CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON]
        // ǲ  [LATIN CAPITAL LETTER D WITH SMALL LETTER Z]
        case '\u01C5' | '\u01F2' => "Dz"

        // ASCII: E

        // È  [LATIN CAPITAL LETTER E WITH GRAVE]
        // É  [LATIN CAPITAL LETTER E WITH ACUTE]
        // Ê  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX]
        // Ë  [LATIN CAPITAL LETTER E WITH DIAERESIS]
        // Ē  [LATIN CAPITAL LETTER E WITH MACRON]
        // Ĕ  [LATIN CAPITAL LETTER E WITH BREVE]
        // Ė  [LATIN CAPITAL LETTER E WITH DOT ABOVE]
        // Ę  [LATIN CAPITAL LETTER E WITH OGONEK]
        // Ě  [LATIN CAPITAL LETTER E WITH CARON]
        // Ǝ  [LATIN CAPITAL LETTER REVERSED E]
        // Ɛ  [LATIN CAPITAL LETTER OPEN E]
        // Ȅ  [LATIN CAPITAL LETTER E WITH DOUBLE GRAVE]
        // Ȇ  [LATIN CAPITAL LETTER E WITH INVERTED BREVE]
        // Ȩ  [LATIN CAPITAL LETTER E WITH CEDILLA]
        // Ɇ  [LATIN CAPITAL LETTER E WITH STROKE]
        // ᴇ  [LATIN LETTER SMALL CAPITAL E]
        // Ḕ  [LATIN CAPITAL LETTER E WITH MACRON AND GRAVE]
        // Ḗ  [LATIN CAPITAL LETTER E WITH MACRON AND ACUTE]
        // Ḙ  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX BELOW]
        // Ḛ  [LATIN CAPITAL LETTER E WITH TILDE BELOW]
        // Ḝ  [LATIN CAPITAL LETTER E WITH CEDILLA AND BREVE]
        // Ẹ  [LATIN CAPITAL LETTER E WITH DOT BELOW]
        // Ẻ  [LATIN CAPITAL LETTER E WITH HOOK ABOVE]
        // Ẽ  [LATIN CAPITAL LETTER E WITH TILDE]
        // Ế  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND ACUTE]
        // Ề  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND GRAVE]
        // Ể  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE]
        // Ễ  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND TILDE]
        // Ệ  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND DOT BELOW]
        // Ⓔ  [CIRCLED LATIN CAPITAL LETTER E]
        // ⱻ  [LATIN LETTER SMALL CAPITAL TURNED E]
        // Ｅ  [FULLWIDTH LATIN CAPITAL LETTER E]
        case '\u00C8' | '\u00C9' | '\u00CA' | '\u00CB' | '\u0112' | '\u0114' | '\u0116' | '\u0118' | '\u011A' | '\u018E' | '\u0190' | '\u0204' | '\u0206' | '\u0228' | '\u0246' | '\u1D07' | '\u1E14' | '\u1E16' | '\u1E18' | '\u1E1A' | '\u1E1C' | '\u1EB8' | '\u1EBA' | '\u1EBC' | '\u1EBE' | '\u1EC0' | '\u1EC2' | '\u1EC4' | '\u1EC6' | '\u24BA' | '\u2C7B' | '\uFF25' => "E"

        // ASCII: F

        // Ƒ  [LATIN CAPITAL LETTER F WITH HOOK]
        // Ḟ  [LATIN CAPITAL LETTER F WITH DOT ABOVE]
        // Ⓕ  [CIRCLED LATIN CAPITAL LETTER F]
        // ꜰ  [LATIN LETTER SMALL CAPITAL F]
        // Ꝼ  [LATIN CAPITAL LETTER INSULAR F]
        // ꟻ  [LATIN EPIGRAPHIC LETTER REVERSED F]
        // Ｆ  [FULLWIDTH LATIN CAPITAL LETTER F]
        case '\u0191' | '\u1E1E' | '\u24BB' | '\uA730' | '\uA77B' | '\uA7FB' | '\uFF26' => "F"

        // ASCII: G

        // Ĝ  [LATIN CAPITAL LETTER G WITH CIRCUMFLEX]
        // Ğ  [LATIN CAPITAL LETTER G WITH BREVE]
        // Ġ  [LATIN CAPITAL LETTER G WITH DOT ABOVE]
        // Ģ  [LATIN CAPITAL LETTER G WITH CEDILLA]
        // Ɠ  [LATIN CAPITAL LETTER G WITH HOOK]
        // Ǥ  [LATIN CAPITAL LETTER G WITH STROKE]
        // ǥ  [LATIN SMALL LETTER G WITH STROKE]
        // Ǧ  [LATIN CAPITAL LETTER G WITH CARON]
        // ǧ  [LATIN SMALL LETTER G WITH CARON]
        // Ǵ  [LATIN CAPITAL LETTER G WITH ACUTE]
        // ɢ  [LATIN LETTER SMALL CAPITAL G]
        // ʛ  [LATIN LETTER SMALL CAPITAL G WITH HOOK]
        // Ḡ  [LATIN CAPITAL LETTER G WITH MACRON]
        // Ⓖ  [CIRCLED LATIN CAPITAL LETTER G]
        // Ᵹ  [LATIN CAPITAL LETTER INSULAR G]
        // Ꝿ  [LATIN CAPITAL LETTER TURNED INSULAR G]
        // Ｇ  [FULLWIDTH LATIN CAPITAL LETTER G]
        case '\u011C' | '\u011E' | '\u0120' | '\u0122' | '\u0193' | '\u01E4' | '\u01E5' | '\u01E6' | '\u01E7' | '\u01F4' | '\u0262' | '\u029B' | '\u1E20' | '\u24BC' | '\uA77D' | '\uA77E' | '\uFF27' => "G"

        // ASCII: H

        // Ĥ  [LATIN CAPITAL LETTER H WITH CIRCUMFLEX]
        // Ħ  [LATIN CAPITAL LETTER H WITH STROKE]
        // Ȟ  [LATIN CAPITAL LETTER H WITH CARON]
        // ʜ  [LATIN LETTER SMALL CAPITAL H]
        // Ḣ  [LATIN CAPITAL LETTER H WITH DOT ABOVE]
        // Ḥ  [LATIN CAPITAL LETTER H WITH DOT BELOW]
        // Ḧ  [LATIN CAPITAL LETTER H WITH DIAERESIS]
        // Ḩ  [LATIN CAPITAL LETTER H WITH CEDILLA]
        // Ḫ  [LATIN CAPITAL LETTER H WITH BREVE BELOW]
        // Ⓗ  [CIRCLED LATIN CAPITAL LETTER H]
        // Ⱨ  [LATIN CAPITAL LETTER H WITH DESCENDER]
        // Ⱶ  [LATIN CAPITAL LETTER HALF H]
        // Ｈ  [FULLWIDTH LATIN CAPITAL LETTER H]
        case '\u0124' | '\u0126' | '\u021E' | '\u029C' | '\u1E22' | '\u1E24' | '\u1E26' | '\u1E28' | '\u1E2A' | '\u24BD' | '\u2C67' | '\u2C75' | '\uFF28' => "H"

        // ASCII: HV

        // Ƕ  http://en.wikipedia.org/wiki/Hwair  [LATIN CAPITAL LETTER HWAIR]
        case '\u01F6' => "HV"

        // ASCII: I

        // Ì  [LATIN CAPITAL LETTER I WITH GRAVE]
        // Í  [LATIN CAPITAL LETTER I WITH ACUTE]
        // Î  [LATIN CAPITAL LETTER I WITH CIRCUMFLEX]
        // Ï  [LATIN CAPITAL LETTER I WITH DIAERESIS]
        // Ĩ  [LATIN CAPITAL LETTER I WITH TILDE]
        // Ī  [LATIN CAPITAL LETTER I WITH MACRON]
        // Ĭ  [LATIN CAPITAL LETTER I WITH BREVE]
        // Į  [LATIN CAPITAL LETTER I WITH OGONEK]
        // İ  [LATIN CAPITAL LETTER I WITH DOT ABOVE]
        // Ɩ  [LATIN CAPITAL LETTER IOTA]
        // Ɨ  [LATIN CAPITAL LETTER I WITH STROKE]
        // Ǐ  [LATIN CAPITAL LETTER I WITH CARON]
        // Ȉ  [LATIN CAPITAL LETTER I WITH DOUBLE GRAVE]
        // Ȋ  [LATIN CAPITAL LETTER I WITH INVERTED BREVE]
        // ɪ  [LATIN LETTER SMALL CAPITAL I]
        // ᵻ  [LATIN SMALL CAPITAL LETTER I WITH STROKE]
        // Ḭ  [LATIN CAPITAL LETTER I WITH TILDE BELOW]
        // Ḯ  [LATIN CAPITAL LETTER I WITH DIAERESIS AND ACUTE]
        // Ỉ  [LATIN CAPITAL LETTER I WITH HOOK ABOVE]
        // Ị  [LATIN CAPITAL LETTER I WITH DOT BELOW]
        // Ⓘ  [CIRCLED LATIN CAPITAL LETTER I]
        // ꟾ  [LATIN EPIGRAPHIC LETTER I LONGA]
        // Ｉ  [FULLWIDTH LATIN CAPITAL LETTER I]
        case '\u00CC' | '\u00CD' | '\u00CE' | '\u00CF' | '\u0128' | '\u012A' | '\u012C' | '\u012E' | '\u0130' | '\u0196' | '\u0197' | '\u01CF' | '\u0208' | '\u020A' | '\u026A' | '\u1D7B' | '\u1E2C' | '\u1E2E' | '\u1EC8' | '\u1ECA' | '\u24BE' | '\uA7FE' | '\uFF29' => "I"

        // ASCII: IJ

        // Ĳ  [LATIN CAPITAL LIGATURE IJ]
        case '\u0132' => "IJ"

        // ASCII: J

        // Ĵ  [LATIN CAPITAL LETTER J WITH CIRCUMFLEX]
        // Ɉ  [LATIN CAPITAL LETTER J WITH STROKE]
        // ᴊ  [LATIN LETTER SMALL CAPITAL J]
        // Ⓙ  [CIRCLED LATIN CAPITAL LETTER J]
        // Ｊ  [FULLWIDTH LATIN CAPITAL LETTER J]
        case '\u0134' | '\u0248' | '\u1D0A' | '\u24BF' | '\uFF2A' => "J"

        // ASCII: K

        // Ķ  [LATIN CAPITAL LETTER K WITH CEDILLA]
        // Ƙ  [LATIN CAPITAL LETTER K WITH HOOK]
        // Ǩ  [LATIN CAPITAL LETTER K WITH CARON]
        // ᴋ  [LATIN LETTER SMALL CAPITAL K]
        // Ḱ  [LATIN CAPITAL LETTER K WITH ACUTE]
        // Ḳ  [LATIN CAPITAL LETTER K WITH DOT BELOW]
        // Ḵ  [LATIN CAPITAL LETTER K WITH LINE BELOW]
        // Ⓚ  [CIRCLED LATIN CAPITAL LETTER K]
        // Ⱪ  [LATIN CAPITAL LETTER K WITH DESCENDER]
        // Ꝁ  [LATIN CAPITAL LETTER K WITH STROKE]
        // Ꝃ  [LATIN CAPITAL LETTER K WITH DIAGONAL STROKE]
        // Ꝅ  [LATIN CAPITAL LETTER K WITH STROKE AND DIAGONAL STROKE]
        // Ｋ  [FULLWIDTH LATIN CAPITAL LETTER K]
        case '\u0136' | '\u0198' | '\u01E8' | '\u1D0B' | '\u1E30' | '\u1E32' | '\u1E34' | '\u24C0' | '\u2C69' | '\uA740' | '\uA742' | '\uA744' | '\uFF2B' => "K"

        // ASCII: L

        // Ĺ  [LATIN CAPITAL LETTER L WITH ACUTE]
        // Ļ  [LATIN CAPITAL LETTER L WITH CEDILLA]
        // Ľ  [LATIN CAPITAL LETTER L WITH CARON]
        // Ŀ  [LATIN CAPITAL LETTER L WITH MIDDLE DOT]
        // Ł  [LATIN CAPITAL LETTER L WITH STROKE]
        // Ƚ  [LATIN CAPITAL LETTER L WITH BAR]
        // ʟ  [LATIN LETTER SMALL CAPITAL L]
        // ᴌ  [LATIN LETTER SMALL CAPITAL L WITH STROKE]
        // Ḷ  [LATIN CAPITAL LETTER L WITH DOT BELOW]
        // Ḹ  [LATIN CAPITAL LETTER L WITH DOT BELOW AND MACRON]
        // Ḻ  [LATIN CAPITAL LETTER L WITH LINE BELOW]
        // Ḽ  [LATIN CAPITAL LETTER L WITH CIRCUMFLEX BELOW]
        // Ⓛ  [CIRCLED LATIN CAPITAL LETTER L]
        // Ⱡ  [LATIN CAPITAL LETTER L WITH DOUBLE BAR]
        // Ɫ  [LATIN CAPITAL LETTER L WITH MIDDLE TILDE]
        // Ꝇ  [LATIN CAPITAL LETTER BROKEN L]
        // Ꝉ  [LATIN CAPITAL LETTER L WITH HIGH STROKE]
        // Ꞁ  [LATIN CAPITAL LETTER TURNED L]
        // Ｌ  [FULLWIDTH LATIN CAPITAL LETTER L]
        case '\u0139' | '\u013B' | '\u013D' | '\u013F' | '\u0141' | '\u023D' | '\u029F' | '\u1D0C' | '\u1E36' | '\u1E38' | '\u1E3A' | '\u1E3C' | '\u24C1' | '\u2C60' | '\u2C62' | '\uA746' | '\uA748' | '\uA780' | '\uFF2C' => "L"

        // ASCII: LJ

        // Ǉ  [LATIN CAPITAL LETTER LJ]
        case '\u01C7' => "LJ"

        // ASCII: LL

        // Ỻ  [LATIN CAPITAL LETTER MIDDLE-WELSH LL]
        case '\u1EFA' => "LL"

        // ASCII: Lj

        // ǈ  [LATIN CAPITAL LETTER L WITH SMALL LETTER J]
        case '\u01C8' => "Lj"

        // ASCII: M

        // Ɯ  [LATIN CAPITAL LETTER TURNED M]
        // ᴍ  [LATIN LETTER SMALL CAPITAL M]
        // Ḿ  [LATIN CAPITAL LETTER M WITH ACUTE]
        // Ṁ  [LATIN CAPITAL LETTER M WITH DOT ABOVE]
        // Ṃ  [LATIN CAPITAL LETTER M WITH DOT BELOW]
        // Ⓜ  [CIRCLED LATIN CAPITAL LETTER M]
        // Ɱ  [LATIN CAPITAL LETTER M WITH HOOK]
        // ꟽ  [LATIN EPIGRAPHIC LETTER INVERTED M]
        // ꟿ  [LATIN EPIGRAPHIC LETTER ARCHAIC M]
        // Ｍ  [FULLWIDTH LATIN CAPITAL LETTER M]
        case '\u019C' | '\u1D0D' | '\u1E3E' | '\u1E40' | '\u1E42' | '\u24C2' | '\u2C6E' | '\uA7FD' | '\uA7FF' | '\uFF2D' => "M"

        // ASCII: N

        // Ñ  [LATIN CAPITAL LETTER N WITH TILDE]
        // Ń  [LATIN CAPITAL LETTER N WITH ACUTE]
        // Ņ  [LATIN CAPITAL LETTER N WITH CEDILLA]
        // Ň  [LATIN CAPITAL LETTER N WITH CARON]
        // Ŋ  http://en.wikipedia.org/wiki/Eng_(letter)  [LATIN CAPITAL LETTER ENG]
        // Ɲ  [LATIN CAPITAL LETTER N WITH LEFT HOOK]
        // Ǹ  [LATIN CAPITAL LETTER N WITH GRAVE]
        // Ƞ  [LATIN CAPITAL LETTER N WITH LONG RIGHT LEG]
        // ɴ  [LATIN LETTER SMALL CAPITAL N]
        // ᴎ  [LATIN LETTER SMALL CAPITAL REVERSED N]
        // Ṅ  [LATIN CAPITAL LETTER N WITH DOT ABOVE]
        // Ṇ  [LATIN CAPITAL LETTER N WITH DOT BELOW]
        // Ṉ  [LATIN CAPITAL LETTER N WITH LINE BELOW]
        // Ṋ  [LATIN CAPITAL LETTER N WITH CIRCUMFLEX BELOW]
        // Ⓝ  [CIRCLED LATIN CAPITAL LETTER N]
        // Ｎ  [FULLWIDTH LATIN CAPITAL LETTER N]
        case '\u00D1' | '\u0143' | '\u0145' | '\u0147' | '\u014A' | '\u019D' | '\u01F8' | '\u0220' | '\u0274' | '\u1D0E' | '\u1E44' | '\u1E46' | '\u1E48' | '\u1E4A' | '\u24C3' | '\uFF2E' => "N"

        // ASCII: NJ

        // Ǌ  [LATIN CAPITAL LETTER NJ]
        case '\u01CA' => "NJ"

        // ASCII: Nj

        // ǋ  [LATIN CAPITAL LETTER N WITH SMALL LETTER J]
        case '\u01CB' => "Nj"

        // ASCII: O

        // Ꜵ  [LATIN CAPITAL LETTER AO]
        // Ò  [LATIN CAPITAL LETTER O WITH GRAVE]
        // Ó  [LATIN CAPITAL LETTER O WITH ACUTE]
        // Ô  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX]
        // Õ  [LATIN CAPITAL LETTER O WITH TILDE]
        // Ö  [LATIN CAPITAL LETTER O WITH DIAERESIS]
        // Ø  [LATIN CAPITAL LETTER O WITH STROKE]
        // Ō  [LATIN CAPITAL LETTER O WITH MACRON]
        // Ŏ  [LATIN CAPITAL LETTER O WITH BREVE]
        // Ő  [LATIN CAPITAL LETTER O WITH DOUBLE ACUTE]
        // Ɔ  [LATIN CAPITAL LETTER OPEN O]
        // Ɵ  [LATIN CAPITAL LETTER O WITH MIDDLE TILDE]
        // Ơ  [LATIN CAPITAL LETTER O WITH HORN]
        // Ǒ  [LATIN CAPITAL LETTER O WITH CARON]
        // Ǫ  [LATIN CAPITAL LETTER O WITH OGONEK]
        // Ǭ  [LATIN CAPITAL LETTER O WITH OGONEK AND MACRON]
        // Ǿ  [LATIN CAPITAL LETTER O WITH STROKE AND ACUTE]
        // Ȍ  [LATIN CAPITAL LETTER O WITH DOUBLE GRAVE]
        // Ȏ  [LATIN CAPITAL LETTER O WITH INVERTED BREVE]
        // Ȫ  [LATIN CAPITAL LETTER O WITH DIAERESIS AND MACRON]
        // Ȭ  [LATIN CAPITAL LETTER O WITH TILDE AND MACRON]
        // Ȯ  [LATIN CAPITAL LETTER O WITH DOT ABOVE]
        // Ȱ  [LATIN CAPITAL LETTER O WITH DOT ABOVE AND MACRON]
        // ᴏ  [LATIN LETTER SMALL CAPITAL O]
        // ᴐ  [LATIN LETTER SMALL CAPITAL OPEN O]
        // Ṍ  [LATIN CAPITAL LETTER O WITH TILDE AND ACUTE]
        // Ṏ  [LATIN CAPITAL LETTER O WITH TILDE AND DIAERESIS]
        // Ṑ  [LATIN CAPITAL LETTER O WITH MACRON AND GRAVE]
        // Ṓ  [LATIN CAPITAL LETTER O WITH MACRON AND ACUTE]
        // Ọ  [LATIN CAPITAL LETTER O WITH DOT BELOW]
        // Ỏ  [LATIN CAPITAL LETTER O WITH HOOK ABOVE]
        // Ố  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND ACUTE]
        // Ồ  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND GRAVE]
        // Ổ  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE]
        // Ỗ  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND TILDE]
        // Ộ  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND DOT BELOW]
        // Ớ  [LATIN CAPITAL LETTER O WITH HORN AND ACUTE]
        // Ờ  [LATIN CAPITAL LETTER O WITH HORN AND GRAVE]
        // Ở  [LATIN CAPITAL LETTER O WITH HORN AND HOOK ABOVE]
        // Ỡ  [LATIN CAPITAL LETTER O WITH HORN AND TILDE]
        // Ợ  [LATIN CAPITAL LETTER O WITH HORN AND DOT BELOW]
        // Ⓞ  [CIRCLED LATIN CAPITAL LETTER O]
        // Ꝋ  [LATIN CAPITAL LETTER O WITH LONG STROKE OVERLAY]
        // Ꝍ  [LATIN CAPITAL LETTER O WITH LOOP]
        // Ｏ  [FULLWIDTH LATIN CAPITAL LETTER O]
        case '\uA734' | '\u00D2' | '\u00D3' | '\u00D4' | '\u00D5' | '\u00D6' | '\u00D8' | '\u014C' | '\u014E' | '\u0150' | '\u0186' | '\u019F' | '\u01A0' | '\u01D1' | '\u01EA' | '\u01EC' | '\u01FE' | '\u020C' | '\u020E' | '\u022A' | '\u022C' | '\u022E' | '\u0230' | '\u1D0F' | '\u1D10' | '\u1E4C' | '\u1E4E' | '\u1E50' | '\u1E52' | '\u1ECC' | '\u1ECE' | '\u1ED0' | '\u1ED2' | '\u1ED4' | '\u1ED6' | '\u1ED8' | '\u1EDA' | '\u1EDC' | '\u1EDE' | '\u1EE0' | '\u1EE2' | '\u24C4' | '\uA74A' | '\uA74C' | '\uFF2F' => "O"

        // ASCII: OE

        // Œ  [LATIN CAPITAL LIGATURE OE]
        // ɶ  [LATIN LETTER SMALL CAPITAL OE]
        case '\u0152' | '\u0276' => "OE"

        // ASCII: OO

        // Ꝏ  [LATIN CAPITAL LETTER OO]
        case '\uA74E' => "OO"

        // ASCII: OU

        // Ȣ  http://en.wikipedia.org/wiki/OU  [LATIN CAPITAL LETTER OU]
        // ᴕ  [LATIN LETTER SMALL CAPITAL OU]
        case '\u0222' | '\u1D15' => "OU"

        // ASCII: P

        // Ƥ  [LATIN CAPITAL LETTER P WITH HOOK]
        // ᴘ  [LATIN LETTER SMALL CAPITAL P]
        // Ṕ  [LATIN CAPITAL LETTER P WITH ACUTE]
        // Ṗ  [LATIN CAPITAL LETTER P WITH DOT ABOVE]
        // Ⓟ  [CIRCLED LATIN CAPITAL LETTER P]
        // Ᵽ  [LATIN CAPITAL LETTER P WITH STROKE]
        // Ꝑ  [LATIN CAPITAL LETTER P WITH STROKE THROUGH DESCENDER]
        // Ꝓ  [LATIN CAPITAL LETTER P WITH FLOURISH]
        // Ꝕ  [LATIN CAPITAL LETTER P WITH SQUIRREL TAIL]
        // Ｐ  [FULLWIDTH LATIN CAPITAL LETTER P]
        case '\u01A4' | '\u1D18' | '\u1E54' | '\u1E56' | '\u24C5' | '\u2C63' | '\uA750' | '\uA752' | '\uA754' | '\uFF30' => "P"

        // ASCII: Q

        // Ɋ  [LATIN CAPITAL LETTER SMALL Q WITH HOOK TAIL]
        // Ⓠ  [CIRCLED LATIN CAPITAL LETTER Q]
        // Ꝗ  [LATIN CAPITAL LETTER Q WITH STROKE THROUGH DESCENDER]
        // Ꝙ  [LATIN CAPITAL LETTER Q WITH DIAGONAL STROKE]
        // Ｑ  [FULLWIDTH LATIN CAPITAL LETTER Q]
        case '\u024A' | '\u24C6' | '\uA756' | '\uA758' | '\uFF31' => "Q"

        // ASCII: R

        // Ŕ  [LATIN CAPITAL LETTER R WITH ACUTE]
        // Ŗ  [LATIN CAPITAL LETTER R WITH CEDILLA]
        // Ř  [LATIN CAPITAL LETTER R WITH CARON]
        // Ȓ  [LATIN CAPITAL LETTER R WITH DOUBLE GRAVE]
        // Ȓ  [LATIN CAPITAL LETTER R WITH INVERTED BREVE]
        // Ɍ  [LATIN CAPITAL LETTER R WITH STROKE]
        // ʀ  [LATIN LETTER SMALL CAPITAL R]
        // ʁ  [LATIN LETTER SMALL CAPITAL INVERTED R]
        // ᴙ  [LATIN LETTER SMALL CAPITAL REVERSED R]
        // ᴚ  [LATIN LETTER SMALL CAPITAL TURNED R]
        // Ṙ  [LATIN CAPITAL LETTER R WITH DOT ABOVE]
        // Ṛ  [LATIN CAPITAL LETTER R WITH DOT BELOW]
        // Ṝ  [LATIN CAPITAL LETTER R WITH DOT BELOW AND MACRON]
        // Ṟ  [LATIN CAPITAL LETTER R WITH LINE BELOW]
        // Ⓡ  [CIRCLED LATIN CAPITAL LETTER R]
        // Ɽ  [LATIN CAPITAL LETTER R WITH TAIL]
        // Ꝛ  [LATIN CAPITAL LETTER R ROTUNDA]
        // Ꞃ  [LATIN CAPITAL LETTER INSULAR R]
        // Ｒ  [FULLWIDTH LATIN CAPITAL LETTER R]
        case '\u0154' | '\u0156' | '\u0158' | '\u0210' | '\u0212' | '\u024C' | '\u0280' | '\u0281' | '\u1D19' | '\u1D1A' | '\u1E58' | '\u1E5A' | '\u1E5C' | '\u1E5E' | '\u24C7' | '\u2C64' | '\uA75A' | '\uA782' | '\uFF32' => "R"

        // ASCII: S

        // Ś  [LATIN CAPITAL LETTER S WITH ACUTE]
        // Ŝ  [LATIN CAPITAL LETTER S WITH CIRCUMFLEX]
        // Ş  [LATIN CAPITAL LETTER S WITH CEDILLA]
        // Š  [LATIN CAPITAL LETTER S WITH CARON]
        // Ș  [LATIN CAPITAL LETTER S WITH COMMA BELOW]
        // Ṡ  [LATIN CAPITAL LETTER S WITH DOT ABOVE]
        // Ṣ  [LATIN CAPITAL LETTER S WITH DOT BELOW]
        // Ṥ  [LATIN CAPITAL LETTER S WITH ACUTE AND DOT ABOVE]
        // Ṧ  [LATIN CAPITAL LETTER S WITH CARON AND DOT ABOVE]
        // Ṩ  [LATIN CAPITAL LETTER S WITH DOT BELOW AND DOT ABOVE]
        // Ⓢ  [CIRCLED LATIN CAPITAL LETTER S]
        // ꜱ  [LATIN LETTER SMALL CAPITAL S]
        // ꞅ  [LATIN SMALL LETTER INSULAR S]
        // Ｓ  [FULLWIDTH LATIN CAPITAL LETTER S]
        case '\u015A' | '\u015C' | '\u015E' | '\u0160' | '\u0218' | '\u1E60' | '\u1E62' | '\u1E64' | '\u1E66' | '\u1E68' | '\u24C8' | '\uA731' | '\uA785' | '\uFF33' => "S"

        // ASCII: SS

        // ẞ  [LATIN CAPITAL LETTER SHARP S]
        case '\u1E9E' => "SS"

        // ASCII: T

        // Ţ  [LATIN CAPITAL LETTER T WITH CEDILLA]
        // Ť  [LATIN CAPITAL LETTER T WITH CARON]
        // Ŧ  [LATIN CAPITAL LETTER T WITH STROKE]
        // Ƭ  [LATIN CAPITAL LETTER T WITH HOOK]
        // Ʈ  [LATIN CAPITAL LETTER T WITH RETROFLEX HOOK]
        // Ț  [LATIN CAPITAL LETTER T WITH COMMA BELOW]
        // Ⱦ  [LATIN CAPITAL LETTER T WITH DIAGONAL STROKE]
        // ᴛ  [LATIN LETTER SMALL CAPITAL T]
        // Ṫ  [LATIN CAPITAL LETTER T WITH DOT ABOVE]
        // Ṭ  [LATIN CAPITAL LETTER T WITH DOT BELOW]
        // Ṯ  [LATIN CAPITAL LETTER T WITH LINE BELOW]
        // Ṱ  [LATIN CAPITAL LETTER T WITH CIRCUMFLEX BELOW]
        // Ⓣ  [CIRCLED LATIN CAPITAL LETTER T]
        // Ꞇ  [LATIN CAPITAL LETTER INSULAR T]
        // Ｔ  [FULLWIDTH LATIN CAPITAL LETTER T]
        case '\u0162' | '\u0164' | '\u0166' | '\u01AC' | '\u01AE' | '\u021A' | '\u023E' | '\u1D1B' | '\u1E6A' | '\u1E6C' | '\u1E6E' | '\u1E70' | '\u24C9' | '\uA786' | '\uFF34' => "T"

        // ASCII: TH

        // Þ  [LATIN CAPITAL LETTER THORN]
        // Ꝧ  [LATIN CAPITAL LETTER THORN WITH STROKE THROUGH DESCENDER]
        case '\u00DE' | '\uA766' => "TH"

        // ASCII: TZ

        // Ꜩ  [LATIN CAPITAL LETTER TZ]
        case '\uA728' => "TZ"

        // ASCII: U

        // Ù  [LATIN CAPITAL LETTER U WITH GRAVE]
        // Ú  [LATIN CAPITAL LETTER U WITH ACUTE]
        // Û  [LATIN CAPITAL LETTER U WITH CIRCUMFLEX]
        // Ü  [LATIN CAPITAL LETTER U WITH DIAERESIS]
        // Ũ  [LATIN CAPITAL LETTER U WITH TILDE]
        // Ū  [LATIN CAPITAL LETTER U WITH MACRON]
        // Ŭ  [LATIN CAPITAL LETTER U WITH BREVE]
        // Ů  [LATIN CAPITAL LETTER U WITH RING ABOVE]
        // Ű  [LATIN CAPITAL LETTER U WITH DOUBLE ACUTE]
        // Ų  [LATIN CAPITAL LETTER U WITH OGONEK]
        // Ư  [LATIN CAPITAL LETTER U WITH HORN]
        // Ǔ  [LATIN CAPITAL LETTER U WITH CARON]
        // Ǖ  [LATIN CAPITAL LETTER U WITH DIAERESIS AND MACRON]
        // Ǘ  [LATIN CAPITAL LETTER U WITH DIAERESIS AND ACUTE]
        // Ǚ  [LATIN CAPITAL LETTER U WITH DIAERESIS AND CARON]
        // Ǜ  [LATIN CAPITAL LETTER U WITH DIAERESIS AND GRAVE]
        // Ȕ  [LATIN CAPITAL LETTER U WITH DOUBLE GRAVE]
        // Ȗ  [LATIN CAPITAL LETTER U WITH INVERTED BREVE]
        // Ʉ  [LATIN CAPITAL LETTER U BAR]
        // ᴜ  [LATIN LETTER SMALL CAPITAL U]
        // ᵾ  [LATIN SMALL CAPITAL LETTER U WITH STROKE]
        // Ṳ  [LATIN CAPITAL LETTER U WITH DIAERESIS BELOW]
        // Ṵ  [LATIN CAPITAL LETTER U WITH TILDE BELOW]
        // Ṷ  [LATIN CAPITAL LETTER U WITH CIRCUMFLEX BELOW]
        // Ṹ  [LATIN CAPITAL LETTER U WITH TILDE AND ACUTE]
        // Ṻ  [LATIN CAPITAL LETTER U WITH MACRON AND DIAERESIS]
        // Ụ  [LATIN CAPITAL LETTER U WITH DOT BELOW]
        // Ủ  [LATIN CAPITAL LETTER U WITH HOOK ABOVE]
        // Ứ  [LATIN CAPITAL LETTER U WITH HORN AND ACUTE]
        // Ừ  [LATIN CAPITAL LETTER U WITH HORN AND GRAVE]
        // Ử  [LATIN CAPITAL LETTER U WITH HORN AND HOOK ABOVE]
        // Ữ  [LATIN CAPITAL LETTER U WITH HORN AND TILDE]
        // Ự  [LATIN CAPITAL LETTER U WITH HORN AND DOT BELOW]
        // Ⓤ  [CIRCLED LATIN CAPITAL LETTER U]
        // Ｕ  [FULLWIDTH LATIN CAPITAL LETTER U]
        case '\u00D9' | '\u00DA' | '\u00DB' | '\u00DC' | '\u0168' | '\u016A' | '\u016C' | '\u016E' | '\u0170' | '\u0172' | '\u01AF' | '\u01D3' | '\u01D5' | '\u01D7' | '\u01D9' | '\u01DB' | '\u0214' | '\u0216' | '\u0244' | '\u1D1C' | '\u1D7E' | '\u1E72' | '\u1E74' | '\u1E76' | '\u1E78' | '\u1E7A' | '\u1EE4' | '\u1EE6' | '\u1EE8' | '\u1EEA' | '\u1EEC' | '\u1EEE' | '\u1EF0' | '\u24CA' | '\uFF35' => "U"

        // ASCII: V

        // Ʋ  [LATIN CAPITAL LETTER V WITH HOOK]
        // Ʌ  [LATIN CAPITAL LETTER TURNED V]
        // ᴠ  [LATIN LETTER SMALL CAPITAL V]
        // Ṽ  [LATIN CAPITAL LETTER V WITH TILDE]
        // Ṿ  [LATIN CAPITAL LETTER V WITH DOT BELOW]
        // Ỽ  [LATIN CAPITAL LETTER MIDDLE-WELSH V]
        // Ⓥ  [CIRCLED LATIN CAPITAL LETTER V]
        // Ꝟ  [LATIN CAPITAL LETTER V WITH DIAGONAL STROKE]
        // Ꝩ  [LATIN CAPITAL LETTER VEND]
        // Ｖ  [FULLWIDTH LATIN CAPITAL LETTER V]
        case '\u01B2' | '\u0245' | '\u1D20' | '\u1E7C' | '\u1E7E' | '\u1EFC' | '\u24CB' | '\uA75E' | '\uA768' | '\uFF36' => "V"

        // ASCII: VY

        // Ꝡ  [LATIN CAPITAL LETTER VY]
        case '\uA760' => "VY"

        // ASCII: W

        // Ŵ  [LATIN CAPITAL LETTER W WITH CIRCUMFLEX]
        // Ƿ  http://en.wikipedia.org/wiki/Wynn  [LATIN CAPITAL LETTER WYNN]
        // ᴡ  [LATIN LETTER SMALL CAPITAL W]
        // Ẁ  [LATIN CAPITAL LETTER W WITH GRAVE]
        // Ẃ  [LATIN CAPITAL LETTER W WITH ACUTE]
        // Ẅ  [LATIN CAPITAL LETTER W WITH DIAERESIS]
        // Ẇ  [LATIN CAPITAL LETTER W WITH DOT ABOVE]
        // Ẉ  [LATIN CAPITAL LETTER W WITH DOT BELOW]
        // Ⓦ  [CIRCLED LATIN CAPITAL LETTER W]
        // Ⱳ  [LATIN CAPITAL LETTER W WITH HOOK]
        // Ｗ  [FULLWIDTH LATIN CAPITAL LETTER W]
        case '\u0174' | '\u01F7' | '\u1D21' | '\u1E80' | '\u1E82' | '\u1E84' | '\u1E86' | '\u1E88' | '\u24CC' | '\u2C72' | '\uFF37' => "W"

        // ASCII: X

        // Ẋ  [LATIN CAPITAL LETTER X WITH DOT ABOVE]
        // Ẍ  [LATIN CAPITAL LETTER X WITH DIAERESIS]
        // Ⓧ  [CIRCLED LATIN CAPITAL LETTER X]
        // Ｘ  [FULLWIDTH LATIN CAPITAL LETTER X]
        case '\u1E8A' | '\u1E8C' | '\u24CD' | '\uFF38' => "X"

        // ASCII: Y

        // Ý  [LATIN CAPITAL LETTER Y WITH ACUTE]
        // Ŷ  [LATIN CAPITAL LETTER Y WITH CIRCUMFLEX]
        // Ÿ  [LATIN CAPITAL LETTER Y WITH DIAERESIS]
        // Ƴ  [LATIN CAPITAL LETTER Y WITH HOOK]
        // Ȳ  [LATIN CAPITAL LETTER Y WITH MACRON]
        // Ɏ  [LATIN CAPITAL LETTER Y WITH STROKE]
        // ʏ  [LATIN LETTER SMALL CAPITAL Y]
        // Ẏ  [LATIN CAPITAL LETTER Y WITH DOT ABOVE]
        // Ỳ  [LATIN CAPITAL LETTER Y WITH GRAVE]
        // Ỵ  [LATIN CAPITAL LETTER Y WITH DOT BELOW]
        // Ỷ  [LATIN CAPITAL LETTER Y WITH HOOK ABOVE]
        // Ỹ  [LATIN CAPITAL LETTER Y WITH TILDE]
        // Ỿ  [LATIN CAPITAL LETTER Y WITH LOOP]
        // Ⓨ  [CIRCLED LATIN CAPITAL LETTER Y]
        // Ｙ  [FULLWIDTH LATIN CAPITAL LETTER Y]
        case '\u00DD' | '\u0176' | '\u0178' | '\u01B3' | '\u0232' | '\u024E' | '\u028F' | '\u1E8E' | '\u1EF2' | '\u1EF4' | '\u1EF6' | '\u1EF8' | '\u1EFE' | '\u24CE' | '\uFF39' => "Y"

        // ASCII: Z

        // Ź  [LATIN CAPITAL LETTER Z WITH ACUTE]
        // Ż  [LATIN CAPITAL LETTER Z WITH DOT ABOVE]
        // Ž  [LATIN CAPITAL LETTER Z WITH CARON]
        // Ƶ  [LATIN CAPITAL LETTER Z WITH STROKE]
        // Ȝ  http://en.wikipedia.org/wiki/Yogh  [LATIN CAPITAL LETTER YOGH]
        // Ȥ  [LATIN CAPITAL LETTER Z WITH HOOK]
        // ᴢ  [LATIN LETTER SMALL CAPITAL Z]
        // Ẑ  [LATIN CAPITAL LETTER Z WITH CIRCUMFLEX]
        // Ẓ  [LATIN CAPITAL LETTER Z WITH DOT BELOW]
        // Ẕ  [LATIN CAPITAL LETTER Z WITH LINE BELOW]
        // Ⓩ  [CIRCLED LATIN CAPITAL LETTER Z]
        // Ⱬ  [LATIN CAPITAL LETTER Z WITH DESCENDER]
        // Ꝣ  [LATIN CAPITAL LETTER VISIGOTHIC Z]
        // Ｚ  [FULLWIDTH LATIN CAPITAL LETTER Z]
        case '\u0179' | '\u017B' | '\u017D' | '\u01B5' | '\u021C' | '\u0224' | '\u1D22' | '\u1E90' | '\u1E92' | '\u1E94' | '\u24CF' | '\u2C6B' | '\uA762' | '\uFF3A' => "Z"

        // ASCII: [

        // ⁅  [LEFT SQUARE BRACKET WITH QUILL]
        // ❲  [LIGHT LEFT TORTOISE SHELL BRACKET ORNAMENT]
        // ［  [FULLWIDTH LEFT SQUARE BRACKET]
        case '\u2045' | '\u2772' | '\uFF3B' => "["

        // ASCII: ]

        // ⁆  [RIGHT SQUARE BRACKET WITH QUILL]
        // ❳  [LIGHT RIGHT TORTOISE SHELL BRACKET ORNAMENT]
        // ］  [FULLWIDTH RIGHT SQUARE BRACKET]
        case '\u2046' | '\u2773' | '\uFF3D' => "]"

        // ASCII: ^

        // ＼  [FULLWIDTH REVERSE SOLIDUS]
        // ‸  [CARET]
        // ＾  [FULLWIDTH CIRCUMFLEX ACCENT]
        case '\uFF3C' | '\u2038' | '\uFF3E' => "^"

        // ASCII: _

        // ＿  [FULLWIDTH LOW LINE]
        case '\uFF3F' => "_"

        // ASCII: a

        // à  [LATIN SMALL LETTER A WITH GRAVE]
        // á  [LATIN SMALL LETTER A WITH ACUTE]
        // â  [LATIN SMALL LETTER A WITH CIRCUMFLEX]
        // ã  [LATIN SMALL LETTER A WITH TILDE]
        // ä  [LATIN SMALL LETTER A WITH DIAERESIS]
        // å  [LATIN SMALL LETTER A WITH RING ABOVE]
        // ā  [LATIN SMALL LETTER A WITH MACRON]
        // ă  [LATIN SMALL LETTER A WITH BREVE]
        // ą  [LATIN SMALL LETTER A WITH OGONEK]
        // ǎ  [LATIN SMALL LETTER A WITH CARON]
        // ǟ  [LATIN SMALL LETTER A WITH DIAERESIS AND MACRON]
        // ǡ  [LATIN SMALL LETTER A WITH DOT ABOVE AND MACRON]
        // ǻ  [LATIN SMALL LETTER A WITH RING ABOVE AND ACUTE]
        // ȁ  [LATIN SMALL LETTER A WITH DOUBLE GRAVE]
        // ȃ  [LATIN SMALL LETTER A WITH INVERTED BREVE]
        // ȧ  [LATIN SMALL LETTER A WITH DOT ABOVE]
        // ɐ  [LATIN SMALL LETTER TURNED A]
        // ə  [LATIN SMALL LETTER SCHWA]
        // ɚ  [LATIN SMALL LETTER SCHWA WITH HOOK]
        // ᶏ  [LATIN SMALL LETTER A WITH RETROFLEX HOOK]
        // ᶕ  [LATIN SMALL LETTER SCHWA WITH RETROFLEX HOOK]
        // ạ  [LATIN SMALL LETTER A WITH RING BELOW]
        // ả  [LATIN SMALL LETTER A WITH RIGHT HALF RING]
        // ạ  [LATIN SMALL LETTER A WITH DOT BELOW]
        // ả  [LATIN SMALL LETTER A WITH HOOK ABOVE]
        // ấ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND ACUTE]
        // ầ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND GRAVE]
        // ẩ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE]
        // ẫ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND TILDE]
        // ậ  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND DOT BELOW]
        // ắ  [LATIN SMALL LETTER A WITH BREVE AND ACUTE]
        // ằ  [LATIN SMALL LETTER A WITH BREVE AND GRAVE]
        // ẳ  [LATIN SMALL LETTER A WITH BREVE AND HOOK ABOVE]
        // ẵ  [LATIN SMALL LETTER A WITH BREVE AND TILDE]
        // ặ  [LATIN SMALL LETTER A WITH BREVE AND DOT BELOW]
        // ₐ  [LATIN SUBSCRIPT SMALL LETTER A]
        // ₔ  [LATIN SUBSCRIPT SMALL LETTER SCHWA]
        // ⓐ  [CIRCLED LATIN SMALL LETTER A]
        // ⱥ  [LATIN SMALL LETTER A WITH STROKE]
        // Ɐ  [LATIN CAPITAL LETTER TURNED A]
        // ａ  [FULLWIDTH LATIN SMALL LETTER A]
        case '\u00E0' | '\u00E1' | '\u00E2' | '\u00E3' | '\u00E4' | '\u00E5' | '\u0101' | '\u0103' | '\u0105' | '\u01CE' | '\u01DF' | '\u01E1' | '\u01FB' | '\u0201' | '\u0203' | '\u0227' | '\u0250' | '\u0259' | '\u025A' | '\u1D8F' | '\u1D95' | '\u1E01' | '\u1E9A' | '\u1EA1' | '\u1EA3' | '\u1EA5' | '\u1EA7' | '\u1EA9' | '\u1EAB' | '\u1EAD' | '\u1EAF' | '\u1EB1' | '\u1EB3' | '\u1EB5' | '\u1EB7' | '\u2090' | '\u2094' | '\u24D0' | '\u2C65' | '\u2C6F' | '\uFF41' => "a"

        // ASCII: aa

        // ꜳ  [LATIN SMALL LETTER AA]
        case '\uA733' => "aa"

        // ASCII: ae

        // æ  [LATIN SMALL LETTER AE]
        // ǣ  [LATIN SMALL LETTER AE WITH MACRON]
        // ǽ  [LATIN SMALL LETTER AE WITH ACUTE]
        // ᴂ  [LATIN SMALL LETTER TURNED AE]
        case '\u00E6' | '\u01E3' | '\u01FD' | '\u1D02' => "ae"

        // ASCII: ao

        // ꜵ  [LATIN SMALL LETTER AO]
        case '\uA735' => "ao"

        // ASCII: au

        // ꜷ  [LATIN SMALL LETTER AU]
        case '\uA737' => "au"

        // ASCII: av

        // ꜹ  [LATIN SMALL LETTER AV]
        // ꜻ  [LATIN SMALL LETTER AV WITH HORIZONTAL BAR]
        case '\uA739' | '\uA73B' => "av"

        // ASCII: ay

        // ꜽ  [LATIN SMALL LETTER AY]
        case '\uA73D' => "ay"

        // ASCII: b

        // ƀ  [LATIN SMALL LETTER B WITH STROKE]
        // ƃ  [LATIN SMALL LETTER B WITH TOPBAR]
        // ɓ  [LATIN SMALL LETTER B WITH HOOK]
        // ᵬ  [LATIN SMALL LETTER B WITH MIDDLE TILDE]
        // ᶀ  [LATIN SMALL LETTER B WITH PALATAL HOOK]
        // ḃ  [LATIN SMALL LETTER B WITH DOT ABOVE]
        // ḅ  [LATIN SMALL LETTER B WITH DOT BELOW]
        // ḇ  [LATIN SMALL LETTER B WITH LINE BELOW]
        // ⓑ  [CIRCLED LATIN SMALL LETTER B]
        // ｂ  [FULLWIDTH LATIN SMALL LETTER B]
        case '\u0180' | '\u0183' | '\u0253' | '\u1D6C' | '\u1D80' | '\u1E03' | '\u1E05' | '\u1E07' | '\u24D1' | '\uFF42' => "b"

        // ASCII: b)

        // ⒝  [PARENTHESIZED LATIN SMALL LETTER B]
        case '\u249D' => "b)"

        // ASCII: c

        // ç  [LATIN SMALL LETTER C WITH CEDILLA]
        // ć  [LATIN SMALL LETTER C WITH ACUTE]
        // ĉ  [LATIN SMALL LETTER C WITH CIRCUMFLEX]
        // ċ  [LATIN SMALL LETTER C WITH DOT ABOVE]
        // č  [LATIN SMALL LETTER C WITH CARON]
        // ƈ  [LATIN SMALL LETTER C WITH HOOK]
        // ȼ  [LATIN SMALL LETTER C WITH STROKE]
        // ɕ  [LATIN SMALL LETTER C WITH CURL]
        // ḉ  [LATIN SMALL LETTER C WITH CEDILLA AND ACUTE]
        // ↄ  [LATIN SMALL LETTER REVERSED C]
        // ⓒ  [CIRCLED LATIN SMALL LETTER C]
        // Ꜿ  [LATIN CAPITAL LETTER REVERSED C WITH DOT]
        // ꜿ  [LATIN SMALL LETTER REVERSED C WITH DOT]
        // ｃ  [FULLWIDTH LATIN SMALL LETTER C]
        case '\u00E7' | '\u0107' | '\u0109' | '\u010B' | '\u010D' | '\u0188' | '\u023C' | '\u0255' | '\u1E09' | '\u2184' | '\u24D2' | '\uA73E' | '\uA73F' | '\uFF43' => "c"

        // ASCII: d

        // ð  [LATIN SMALL LETTER ETH]
        // ď  [LATIN SMALL LETTER D WITH CARON]
        // đ  [LATIN SMALL LETTER D WITH STROKE]
        // ƌ  [LATIN SMALL LETTER D WITH TOPBAR]
        // ȡ  [LATIN SMALL LETTER D WITH CURL]
        // ɖ  [LATIN SMALL LETTER D WITH TAIL]
        // ɗ  [LATIN SMALL LETTER D WITH HOOK]
        // ᵭ  [LATIN SMALL LETTER D WITH MIDDLE TILDE]
        // ᶁ  [LATIN SMALL LETTER D WITH PALATAL HOOK]
        // ᶑ  [LATIN SMALL LETTER D WITH HOOK AND TAIL]
        // ḋ  [LATIN SMALL LETTER D WITH DOT ABOVE]
        // ḍ  [LATIN SMALL LETTER D WITH DOT BELOW]
        // ḏ  [LATIN SMALL LETTER D WITH LINE BELOW]
        // ḑ  [LATIN SMALL LETTER D WITH CEDILLA]
        // ḓ  [LATIN SMALL LETTER D WITH CIRCUMFLEX BELOW]
        // ⓓ  [CIRCLED LATIN SMALL LETTER D]
        // ꝺ  [LATIN SMALL LETTER INSULAR D]
        // ｄ  [FULLWIDTH LATIN SMALL LETTER D]
        case '\u00F0' | '\u010F' | '\u0111' | '\u018C' | '\u0221' | '\u0256' | '\u0257' | '\u1D6D' | '\u1D81' | '\u1D91' | '\u1E0B' | '\u1E0D' | '\u1E0F' | '\u1E11' | '\u1E13' | '\u24D3' | '\uA77A' | '\uFF44' => "d"

        // ASCII: db

        // ȸ  [LATIN SMALL LETTER DB DIGRAPH]
        case '\u0238' => "db"

        // ASCII: dz

        // ǆ  [LATIN SMALL LETTER DZ WITH CARON]
        // ǳ  [LATIN SMALL LETTER DZ]
        // ʣ  [LATIN SMALL LETTER DZ DIGRAPH]
        // ʥ  [LATIN SMALL LETTER DZ DIGRAPH WITH CURL]
        case '\u01C6' | '\u01F3' | '\u02A3' | '\u02A5' => "dz"

        // ASCII: e

        // è  [LATIN SMALL LETTER E WITH GRAVE]
        // é  [LATIN SMALL LETTER E WITH ACUTE]
        // ê  [LATIN SMALL LETTER E WITH CIRCUMFLEX]
        // ë  [LATIN SMALL LETTER E WITH DIAERESIS]
        // ē  [LATIN SMALL LETTER E WITH MACRON]
        // ĕ  [LATIN SMALL LETTER E WITH BREVE]
        // ė  [LATIN SMALL LETTER E WITH DOT ABOVE]
        // ę  [LATIN SMALL LETTER E WITH OGONEK]
        // ě  [LATIN SMALL LETTER E WITH CARON]
        // ǝ  [LATIN SMALL LETTER TURNED E]
        // ȅ  [LATIN SMALL LETTER E WITH DOUBLE GRAVE]
        // ȇ  [LATIN SMALL LETTER E WITH INVERTED BREVE]
        // ȩ  [LATIN SMALL LETTER E WITH CEDILLA]
        // ɇ  [LATIN SMALL LETTER E WITH STROKE]
        // ɘ  [LATIN SMALL LETTER REVERSED E]
        // ɛ  [LATIN SMALL LETTER OPEN E]
        // ɜ  [LATIN SMALL LETTER REVERSED OPEN E]
        // ɝ  [LATIN SMALL LETTER REVERSED OPEN E WITH HOOK]
        // ɞ  [LATIN SMALL LETTER CLOSED REVERSED OPEN E]
        // ʚ  [LATIN SMALL LETTER CLOSED OPEN E]
        // ᴈ  [LATIN SMALL LETTER TURNED OPEN E]
        // ᶒ  [LATIN SMALL LETTER E WITH RETROFLEX HOOK]
        // ᶓ  [LATIN SMALL LETTER OPEN E WITH RETROFLEX HOOK]
        // ᶔ  [LATIN SMALL LETTER REVERSED OPEN E WITH RETROFLEX HOOK]
        // ḕ  [LATIN SMALL LETTER E WITH MACRON AND GRAVE]
        // ḗ  [LATIN SMALL LETTER E WITH MACRON AND ACUTE]
        // ḙ  [LATIN SMALL LETTER E WITH CIRCUMFLEX BELOW]
        // ḛ  [LATIN SMALL LETTER E WITH TILDE BELOW]
        // ḝ  [LATIN SMALL LETTER E WITH CEDILLA AND BREVE]
        // ẹ  [LATIN SMALL LETTER E WITH DOT BELOW]
        // ẻ  [LATIN SMALL LETTER E WITH HOOK ABOVE]
        // ẽ  [LATIN SMALL LETTER E WITH TILDE]
        // ế  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND ACUTE]
        // ề  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND GRAVE]
        // ể  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE]
        // ễ  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND TILDE]
        // ệ  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND DOT BELOW]
        // ₑ  [LATIN SUBSCRIPT SMALL LETTER E]
        // ⓔ  [CIRCLED LATIN SMALL LETTER E]
        // ⱸ  [LATIN SMALL LETTER E WITH NOTCH]
        // ｅ  [FULLWIDTH LATIN SMALL LETTER E]
        case '\u00E8' | '\u00E9' | '\u00EA' | '\u00EB' | '\u0113' | '\u0115' | '\u0117' | '\u0119' | '\u011B' | '\u01DD' | '\u0205' | '\u0207' | '\u0229' | '\u0247' | '\u0258' | '\u025B' | '\u025C' | '\u025D' | '\u025E' | '\u029A' | '\u1D08' | '\u1D92' | '\u1D93' | '\u1D94' | '\u1E15' | '\u1E17' | '\u1E19' | '\u1E1B' | '\u1E1D' | '\u1EB9' | '\u1EBB' | '\u1EBD' | '\u1EBF' | '\u1EC1' | '\u1EC3' | '\u1EC5' | '\u1EC7' | '\u2091' | '\u24D4' | '\u2C78' | '\uFF45' => "e"

        // ASCII: f

        // ƒ  [LATIN SMALL LETTER F WITH HOOK]
        // ᵮ  [LATIN SMALL LETTER F WITH MIDDLE TILDE]
        // ᶂ  [LATIN SMALL LETTER F WITH PALATAL HOOK]
        // ḟ  [LATIN SMALL LETTER F WITH DOT ABOVE]
        // ẛ  [LATIN SMALL LETTER LONG S WITH DOT ABOVE]
        // ⓕ  [CIRCLED LATIN SMALL LETTER F]
        // ꝼ  [LATIN SMALL LETTER INSULAR F]
        // ｆ  [FULLWIDTH LATIN SMALL LETTER F]
        case '\u0192' | '\u1D6E' | '\u1D82' | '\u1E1F' | '\u1E9B' | '\u24D5' | '\uA77C' | '\uFF46' => "f"

        // ASCII: ff

        // ﬀ  [LATIN SMALL LIGATURE FF]
        case '\uFB00' => "ff"

        // ASCII: ffi

        // ﬃ  [LATIN SMALL LIGATURE FFI]
        case '\uFB03' => "ffi"

        // ASCII: ffl

        // ﬄ  [LATIN SMALL LIGATURE FFL]
        case '\uFB04' => "ffl"

        // ASCII: fi

        // ﬁ  [LATIN SMALL LIGATURE FI]
        case '\uFB01' => "fi"

        // ASCII: fl

        // ﬂ  [LATIN SMALL LIGATURE FL]
        case '\uFB02' => "fl"

        // ASCII: g

        // ĝ  [LATIN SMALL LETTER G WITH CIRCUMFLEX]
        // ğ  [LATIN SMALL LETTER G WITH BREVE]
        // ġ  [LATIN SMALL LETTER G WITH DOT ABOVE]
        // ģ  [LATIN SMALL LETTER G WITH CEDILLA]
        // ǵ  [LATIN SMALL LETTER G WITH ACUTE]
        // ɠ  [LATIN SMALL LETTER G WITH HOOK]
        // ɡ  [LATIN SMALL LETTER SCRIPT G]
        // ᵷ  [LATIN SMALL LETTER TURNED G]
        // ᵹ  [LATIN SMALL LETTER INSULAR G]
        // ᶃ  [LATIN SMALL LETTER G WITH PALATAL HOOK]
        // ḡ  [LATIN SMALL LETTER G WITH MACRON]
        // ⓖ  [CIRCLED LATIN SMALL LETTER G]
        // ꝿ  [LATIN SMALL LETTER TURNED INSULAR G]
        // ｇ  [FULLWIDTH LATIN SMALL LETTER G]
        case '\u011D' | '\u011F' | '\u0121' | '\u0123' | '\u01F5' | '\u0260' | '\u0261' | '\u1D77' | '\u1D79' | '\u1D83' | '\u1E21' | '\u24D6' | '\uA77F' | '\uFF47' => "g"

        // ASCII: h

        // ĥ  [LATIN SMALL LETTER H WITH CIRCUMFLEX]
        // ħ  [LATIN SMALL LETTER H WITH STROKE]
        // ȟ  [LATIN SMALL LETTER H WITH CARON]
        // ɥ  [LATIN SMALL LETTER TURNED H]
        // ɦ  [LATIN SMALL LETTER H WITH HOOK]
        // ʮ  [LATIN SMALL LETTER TURNED H WITH FISHHOOK]
        // ʯ  [LATIN SMALL LETTER TURNED H WITH FISHHOOK AND TAIL]
        // ḣ  [LATIN SMALL LETTER H WITH DOT ABOVE]
        // ḥ  [LATIN SMALL LETTER H WITH DOT BELOW]
        // ḧ  [LATIN SMALL LETTER H WITH DIAERESIS]
        // ḩ  [LATIN SMALL LETTER H WITH CEDILLA]
        // ḫ  [LATIN SMALL LETTER H WITH BREVE BELOW]
        // ẖ  [LATIN SMALL LETTER H WITH LINE BELOW]
        // ⓗ  [CIRCLED LATIN SMALL LETTER H]
        // ⱨ  [LATIN SMALL LETTER H WITH DESCENDER]
        // ⱶ  [LATIN SMALL LETTER HALF H]
        // ｈ  [FULLWIDTH LATIN SMALL LETTER H]
        case '\u0125' | '\u0127' | '\u021F' | '\u0265' | '\u0266' | '\u02AE' | '\u02AF' | '\u1E23' | '\u1E25' | '\u1E27' | '\u1E29' | '\u1E2B' | '\u1E96' | '\u24D7' | '\u2C68' | '\u2C76' | '\uFF48' => "h"

        // ASCII: hv

        // ƕ  [LATIN SMALL LETTER HV]
        case '\u0195' => "hv"

        // ASCII: i

        // ì  [LATIN SMALL LETTER I WITH GRAVE]
        // í  [LATIN SMALL LETTER I WITH ACUTE]
        // î  [LATIN SMALL LETTER I WITH CIRCUMFLEX]
        // ï  [LATIN SMALL LETTER I WITH DIAERESIS]
        // ĩ  [LATIN SMALL LETTER I WITH TILDE]
        // ī  [LATIN SMALL LETTER I WITH MACRON]
        // ĭ  [LATIN SMALL LETTER I WITH BREVE]
        // į  [LATIN SMALL LETTER I WITH OGONEK]
        // ı  [LATIN SMALL LETTER DOTLESS I]
        // ǐ  [LATIN SMALL LETTER I WITH CARON]
        // ȉ  [LATIN SMALL LETTER I WITH DOUBLE GRAVE]
        // ȋ  [LATIN SMALL LETTER I WITH INVERTED BREVE]
        // ɨ  [LATIN SMALL LETTER I WITH STROKE]
        // ᴉ  [LATIN SMALL LETTER TURNED I]
        // ᵢ  [LATIN SUBSCRIPT SMALL LETTER I]
        // ᵼ  [LATIN SMALL LETTER IOTA WITH STROKE]
        // ᶖ  [LATIN SMALL LETTER I WITH RETROFLEX HOOK]
        // ḭ  [LATIN SMALL LETTER I WITH TILDE BELOW]
        // ḯ  [LATIN SMALL LETTER I WITH DIAERESIS AND ACUTE]
        // ỉ  [LATIN SMALL LETTER I WITH HOOK ABOVE]
        // ị  [LATIN SMALL LETTER I WITH DOT BELOW]
        // ⁱ  [SUPERSCRIPT LATIN SMALL LETTER I]
        // ⓘ  [CIRCLED LATIN SMALL LETTER I]
        // ｉ  [FULLWIDTH LATIN SMALL LETTER I]
        case '\u00EC' | '\u00ED' | '\u00EE' | '\u00EF' | '\u0129' | '\u012B' | '\u012D' | '\u012F' | '\u0131' | '\u01D0' | '\u0209' | '\u020B' | '\u0268' | '\u1D09' | '\u1D62' | '\u1D7C' | '\u1D96' | '\u1E2D' | '\u1E2F' | '\u1EC9' | '\u1ECB' | '\u2071' | '\u24D8' | '\uFF49' => "i"

        // ASCII: ij

        // ĳ  [LATIN SMALL LIGATURE IJ]
        case '\u0133' => "ij"

        // ASCII: j

        // ĵ  [LATIN SMALL LETTER J WITH CIRCUMFLEX]
        // ǰ  [LATIN SMALL LETTER J WITH CARON]
        // ȷ  [LATIN SMALL LETTER DOTLESS J]
        // ɉ  [LATIN SMALL LETTER J WITH STROKE]
        // ɟ  [LATIN SMALL LETTER DOTLESS J WITH STROKE]
        // ʄ  [LATIN SMALL LETTER DOTLESS J WITH STROKE AND HOOK]
        // ʝ  [LATIN SMALL LETTER J WITH CROSSED-TAIL]
        // ⓙ  [CIRCLED LATIN SMALL LETTER J]
        // ⱼ  [LATIN SUBSCRIPT SMALL LETTER J]
        // ｊ  [FULLWIDTH LATIN SMALL LETTER J]
        case '\u0135' | '\u01F0' | '\u0237' | '\u0249' | '\u025F' | '\u0284' | '\u029D' | '\u24D9' | '\u2C7C' | '\uFF4A' => "j"

        // ASCII: k

        // ķ  [LATIN SMALL LETTER K WITH CEDILLA]
        // ƙ  [LATIN SMALL LETTER K WITH HOOK]
        // ǩ  [LATIN SMALL LETTER K WITH CARON]
        // ʞ  [LATIN SMALL LETTER TURNED K]
        // ᶄ  [LATIN SMALL LETTER K WITH PALATAL HOOK]
        // ḱ  [LATIN SMALL LETTER K WITH ACUTE]
        // ḳ  [LATIN SMALL LETTER K WITH DOT BELOW]
        // ḵ  [LATIN SMALL LETTER K WITH LINE BELOW]
        // ⓚ  [CIRCLED LATIN SMALL LETTER K]
        // ⱪ  [LATIN SMALL LETTER K WITH DESCENDER]
        // ꝁ  [LATIN SMALL LETTER K WITH STROKE]
        // ꝃ  [LATIN SMALL LETTER K WITH DIAGONAL STROKE]
        // ꝅ  [LATIN SMALL LETTER K WITH STROKE AND DIAGONAL STROKE]
        // ｋ  [FULLWIDTH LATIN SMALL LETTER K]
        case '\u0137' | '\u0199' | '\u01E9' | '\u029E' | '\u1D84' | '\u1E31' | '\u1E33' | '\u1E35' | '\u24DA' | '\u2C6A' | '\uA741' | '\uA743' | '\uA745' | '\uFF4B' => "k"

        // ASCII: l

        // ĺ  [LATIN SMALL LETTER L WITH ACUTE]
        // ļ  [LATIN SMALL LETTER L WITH CEDILLA]
        // ľ  [LATIN SMALL LETTER L WITH CARON]
        // ŀ  [LATIN SMALL LETTER L WITH MIDDLE DOT]
        // ł  [LATIN SMALL LETTER L WITH STROKE]
        // ƚ  [LATIN SMALL LETTER L WITH BAR]
        // ȴ  [LATIN SMALL LETTER L WITH CURL]
        // ɫ  [LATIN SMALL LETTER L WITH MIDDLE TILDE]
        // ɬ  [LATIN SMALL LETTER L WITH BELT]
        // ɭ  [LATIN SMALL LETTER L WITH RETROFLEX HOOK]
        // ᶅ  [LATIN SMALL LETTER L WITH PALATAL HOOK]
        // ḷ  [LATIN SMALL LETTER L WITH DOT BELOW]
        // ḹ  [LATIN SMALL LETTER L WITH DOT BELOW AND MACRON]
        // ḻ  [LATIN SMALL LETTER L WITH LINE BELOW]
        // ḽ  [LATIN SMALL LETTER L WITH CIRCUMFLEX BELOW]
        // ⓛ  [CIRCLED LATIN SMALL LETTER L]
        // ⱡ  [LATIN SMALL LETTER L WITH DOUBLE BAR]
        // ꝇ  [LATIN SMALL LETTER BROKEN L]
        // ꝉ  [LATIN SMALL LETTER L WITH HIGH STROKE]
        // ꞁ  [LATIN SMALL LETTER TURNED L]
        // ｌ  [FULLWIDTH LATIN SMALL LETTER L]
        case '\u013A' | '\u013C' | '\u013E' | '\u0140' | '\u0142' | '\u019A' | '\u0234' | '\u026B' | '\u026C' | '\u026D' | '\u1D85' | '\u1E37' | '\u1E39' | '\u1E3B' | '\u1E3D' | '\u24DB' | '\u2C61' | '\uA747' | '\uA749' | '\uA781' | '\uFF4C' => "l"

        // ASCII: lj

        // ǉ  [LATIN SMALL LETTER LJ]
        case '\u01C9' => "lj"

        // ASCII: ll

        // ỻ  [LATIN SMALL LETTER MIDDLE-WELSH LL]
        case '\u1EFB' => "ll"

        // ASCII: ls

        // ʪ  [LATIN SMALL LETTER LS DIGRAPH]
        case '\u02AA' => "ls"

        // ASCII: lz

        // ʫ  [LATIN SMALL LETTER LZ DIGRAPH]
        case '\u02AB' => "lz"

        // ASCII: m

        // ɯ  [LATIN SMALL LETTER TURNED M]
        // ɰ  [LATIN SMALL LETTER TURNED M WITH LONG LEG]
        // ɱ  [LATIN SMALL LETTER M WITH HOOK]
        // ᵯ  [LATIN SMALL LETTER M WITH MIDDLE TILDE]
        // ᶆ  [LATIN SMALL LETTER M WITH PALATAL HOOK]
        // ḿ  [LATIN SMALL LETTER M WITH ACUTE]
        // ṁ  [LATIN SMALL LETTER M WITH DOT ABOVE]
        // ṃ  [LATIN SMALL LETTER M WITH DOT BELOW]
        // ⓜ  [CIRCLED LATIN SMALL LETTER M]
        // ｍ  [FULLWIDTH LATIN SMALL LETTER M]
        case '\u026F' | '\u0270' | '\u0271' | '\u1D6F' | '\u1D86' | '\u1E3F' | '\u1E41' | '\u1E43' | '\u24DC' | '\uFF4D' => "m"

        // ASCII: n

        // ñ  [LATIN SMALL LETTER N WITH TILDE]
        // ń  [LATIN SMALL LETTER N WITH ACUTE]
        // ņ  [LATIN SMALL LETTER N WITH CEDILLA]
        // ň  [LATIN SMALL LETTER N WITH CARON]
        // ŉ  [LATIN SMALL LETTER N PRECEDED BY APOSTROPHE]
        // ŋ  http://en.wikipedia.org/wiki/Eng_(letter)  [LATIN SMALL LETTER ENG]
        // ƞ  [LATIN SMALL LETTER N WITH LONG RIGHT LEG]
        // ǹ  [LATIN SMALL LETTER N WITH GRAVE]
        // ȵ  [LATIN SMALL LETTER N WITH CURL]
        // ɲ  [LATIN SMALL LETTER N WITH LEFT HOOK]
        // ɳ  [LATIN SMALL LETTER N WITH RETROFLEX HOOK]
        // ᵰ  [LATIN SMALL LETTER N WITH MIDDLE TILDE]
        // ᶇ  [LATIN SMALL LETTER N WITH PALATAL HOOK]
        // ṅ  [LATIN SMALL LETTER N WITH DOT ABOVE]
        // ṇ  [LATIN SMALL LETTER N WITH DOT BELOW]
        // ṉ  [LATIN SMALL LETTER N WITH LINE BELOW]
        // ṋ  [LATIN SMALL LETTER N WITH CIRCUMFLEX BELOW]
        // ⁿ  [SUPERSCRIPT LATIN SMALL LETTER N]
        // ⓝ  [CIRCLED LATIN SMALL LETTER N]
        // ｎ  [FULLWIDTH LATIN SMALL LETTER N]
        case '\u00F1' | '\u0144' | '\u0146' | '\u0148' | '\u0149' | '\u014B' | '\u019E' | '\u01F9' | '\u0235' | '\u0272' | '\u0273' | '\u1D70' | '\u1D87' | '\u1E45' | '\u1E47' | '\u1E49' | '\u1E4B' | '\u207F' | '\u24DD' | '\uFF4E' => "n"

        // ASCII: nj

        // ǌ  [LATIN SMALL LETTER NJ]
        case '\u01CC' => "nj"

        // ASCII: o

        // ò  [LATIN SMALL LETTER O WITH GRAVE]
        // ó  [LATIN SMALL LETTER O WITH ACUTE]
        // ô  [LATIN SMALL LETTER O WITH CIRCUMFLEX]
        // õ  [LATIN SMALL LETTER O WITH TILDE]
        // ö  [LATIN SMALL LETTER O WITH DIAERESIS]
        // ø  [LATIN SMALL LETTER O WITH STROKE]
        // ō  [LATIN SMALL LETTER O WITH MACRON]
        // ŏ  [LATIN SMALL LETTER O WITH BREVE]
        // ő  [LATIN SMALL LETTER O WITH DOUBLE ACUTE]
        // ơ  [LATIN SMALL LETTER O WITH HORN]
        // ǒ  [LATIN SMALL LETTER O WITH CARON]
        // ǫ  [LATIN SMALL LETTER O WITH OGONEK]
        // ǭ  [LATIN SMALL LETTER O WITH OGONEK AND MACRON]
        // ǿ  [LATIN SMALL LETTER O WITH STROKE AND ACUTE]
        // ȍ  [LATIN SMALL LETTER O WITH DOUBLE GRAVE]
        // ȏ  [LATIN SMALL LETTER O WITH INVERTED BREVE]
        // ȫ  [LATIN SMALL LETTER O WITH DIAERESIS AND MACRON]
        // ȭ  [LATIN SMALL LETTER O WITH TILDE AND MACRON]
        // ȯ  [LATIN SMALL LETTER O WITH DOT ABOVE]
        // ȱ  [LATIN SMALL LETTER O WITH DOT ABOVE AND MACRON]
        // ɔ  [LATIN SMALL LETTER OPEN O]
        // ɵ  [LATIN SMALL LETTER BARRED O]
        // ᴖ  [LATIN SMALL LETTER TOP HALF O]
        // ᴗ  [LATIN SMALL LETTER BOTTOM HALF O]
        // ᶗ  [LATIN SMALL LETTER OPEN O WITH RETROFLEX HOOK]
        // ṍ  [LATIN SMALL LETTER O WITH TILDE AND ACUTE]
        // ṏ  [LATIN SMALL LETTER O WITH TILDE AND DIAERESIS]
        // ṑ  [LATIN SMALL LETTER O WITH MACRON AND GRAVE]
        // ṓ  [LATIN SMALL LETTER O WITH MACRON AND ACUTE]
        // ọ  [LATIN SMALL LETTER O WITH DOT BELOW]
        // ỏ  [LATIN SMALL LETTER O WITH HOOK ABOVE]
        // ố  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND ACUTE]
        // ồ  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND GRAVE]
        // ổ  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE]
        // ỗ  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND TILDE]
        // ộ  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND DOT BELOW]
        // ớ  [LATIN SMALL LETTER O WITH HORN AND ACUTE]
        // ờ  [LATIN SMALL LETTER O WITH HORN AND GRAVE]
        // ở  [LATIN SMALL LETTER O WITH HORN AND HOOK ABOVE]
        // ỡ  [LATIN SMALL LETTER O WITH HORN AND TILDE]
        // ợ  [LATIN SMALL LETTER O WITH HORN AND DOT BELOW]
        // ₒ  [LATIN SUBSCRIPT SMALL LETTER O]
        // ⓞ  [CIRCLED LATIN SMALL LETTER O]
        // ⱺ  [LATIN SMALL LETTER O WITH LOW RING INSIDE]
        // ꝋ  [LATIN SMALL LETTER O WITH LONG STROKE OVERLAY]
        // ꝍ  [LATIN SMALL LETTER O WITH LOOP]
        // ｏ  [FULLWIDTH LATIN SMALL LETTER O]
        case '\u00F2' | '\u00F3' | '\u00F4' | '\u00F5' | '\u00F6' | '\u00F8' | '\u014D' | '\u014F' | '\u0151' | '\u01A1' | '\u01D2' | '\u01EB' | '\u01ED' | '\u01FF' | '\u020D' | '\u020F' | '\u022B' | '\u022D' | '\u022F' | '\u0231' | '\u0254' | '\u0275' | '\u1D16' | '\u1D17' | '\u1D97' | '\u1E4D' | '\u1E4F' | '\u1E51' | '\u1E53' | '\u1ECD' | '\u1ECF' | '\u1ED1' | '\u1ED3' | '\u1ED5' | '\u1ED7' | '\u1ED9' | '\u1EDB' | '\u1EDD' | '\u1EDF' | '\u1EE1' | '\u1EE3' | '\u2092' | '\u24DE' | '\u2C7A' | '\uA74B' | '\uA74D' | '\uFF4F' => "o"

        // ASCII: oe

        // œ  [LATIN SMALL LIGATURE OE]
        // ᴔ  [LATIN SMALL LETTER TURNED OE]
        case '\u0153' | '\u1D14' => "oe"

        // ASCII: oo

        // ꝏ  [LATIN SMALL LETTER OO]
        case '\uA74F' => "oo"

        // ASCII: ou

        // ȣ  http://en.wikipedia.org/wiki/OU  [LATIN SMALL LETTER OU]
        case '\u0223' => "ou"

        // ASCII: p

        // ƥ  [LATIN SMALL LETTER P WITH HOOK]
        // ᵱ  [LATIN SMALL LETTER P WITH MIDDLE TILDE]
        // ᵽ  [LATIN SMALL LETTER P WITH STROKE]
        // ᶈ  [LATIN SMALL LETTER P WITH PALATAL HOOK]
        // ṕ  [LATIN SMALL LETTER P WITH ACUTE]
        // ṗ  [LATIN SMALL LETTER P WITH DOT ABOVE]
        // ⓟ  [CIRCLED LATIN SMALL LETTER P]
        // ꝑ  [LATIN SMALL LETTER P WITH STROKE THROUGH DESCENDER]
        // ꝓ  [LATIN SMALL LETTER P WITH FLOURISH]
        // ꝕ  [LATIN SMALL LETTER P WITH SQUIRREL TAIL]
        // ꟼ  [LATIN EPIGRAPHIC LETTER REVERSED P]
        // ｐ  [FULLWIDTH LATIN SMALL LETTER P]
        case '\u01A5' | '\u1D71' | '\u1D7D' | '\u1D88' | '\u1E55' | '\u1E57' | '\u24DF' | '\uA751' | '\uA753' | '\uA755' | '\uA7FC' | '\uFF50' => "p"

        // ASCII: q

        // ĸ  http://en.wikipedia.org/wiki/Kra_(letter)  [LATIN SMALL LETTER KRA]
        // ɋ  [LATIN SMALL LETTER Q WITH HOOK TAIL]
        // ʠ  [LATIN SMALL LETTER Q WITH HOOK]
        // ⓠ  [CIRCLED LATIN SMALL LETTER Q]
        // ꝗ  [LATIN SMALL LETTER Q WITH STROKE THROUGH DESCENDER]
        // ꝙ  [LATIN SMALL LETTER Q WITH DIAGONAL STROKE]
        // ｑ  [FULLWIDTH LATIN SMALL LETTER Q]
        case '\u0138' | '\u024B' | '\u02A0' | '\u24E0' | '\uA757' | '\uA759' | '\uFF51' => "q"

        // ASCII: qp

        // ȹ  [LATIN SMALL LETTER QP DIGRAPH]
        case '\u0239' => "qp"

        // ASCII: r

        // ŕ  [LATIN SMALL LETTER R WITH ACUTE]
        // ŗ  [LATIN SMALL LETTER R WITH CEDILLA]
        // ř  [LATIN SMALL LETTER R WITH CARON]
        // ȑ  [LATIN SMALL LETTER R WITH DOUBLE GRAVE]
        // ȓ  [LATIN SMALL LETTER R WITH INVERTED BREVE]
        // ɍ  [LATIN SMALL LETTER R WITH STROKE]
        // ɼ  [LATIN SMALL LETTER R WITH LONG LEG]
        // ɽ  [LATIN SMALL LETTER R WITH TAIL]
        // ɾ  [LATIN SMALL LETTER R WITH FISHHOOK]
        // ɿ  [LATIN SMALL LETTER REVERSED R WITH FISHHOOK]
        // ᵣ  [LATIN SUBSCRIPT SMALL LETTER R]
        // ᵲ  [LATIN SMALL LETTER R WITH MIDDLE TILDE]
        // ᵳ  [LATIN SMALL LETTER R WITH FISHHOOK AND MIDDLE TILDE]
        // ᶉ  [LATIN SMALL LETTER R WITH PALATAL HOOK]
        // ṙ  [LATIN SMALL LETTER R WITH DOT ABOVE]
        // ṛ  [LATIN SMALL LETTER R WITH DOT BELOW]
        // ṝ  [LATIN SMALL LETTER R WITH DOT BELOW AND MACRON]
        // ṟ  [LATIN SMALL LETTER R WITH LINE BELOW]
        // ⓡ  [CIRCLED LATIN SMALL LETTER R]
        // ꝛ  [LATIN SMALL LETTER R ROTUNDA]
        // ꞃ  [LATIN SMALL LETTER INSULAR R]
        // ｒ  [FULLWIDTH LATIN SMALL LETTER R]
        case '\u0155' | '\u0157' | '\u0159' | '\u0211' | '\u0213' | '\u024D' | '\u027C' | '\u027D' | '\u027E' | '\u027F' | '\u1D63' | '\u1D72' | '\u1D73' | '\u1D89' | '\u1E59' | '\u1E5B' | '\u1E5D' | '\u1E5F' | '\u24E1' | '\uA75B' | '\uA783' | '\uFF52' => "r"

        // ASCII: s

        // ś  [LATIN SMALL LETTER S WITH ACUTE]
        // ŝ  [LATIN SMALL LETTER S WITH CIRCUMFLEX]
        // ş  [LATIN SMALL LETTER S WITH CEDILLA]
        // š  [LATIN SMALL LETTER S WITH CARON]
        // ſ  http://en.wikipedia.org/wiki/Long_S  [LATIN SMALL LETTER LONG S]
        // ș  [LATIN SMALL LETTER S WITH COMMA BELOW]
        // ȿ  [LATIN SMALL LETTER S WITH SWASH TAIL]
        // ʂ  [LATIN SMALL LETTER S WITH HOOK]
        // ᵴ  [LATIN SMALL LETTER S WITH MIDDLE TILDE]
        // ᶊ  [LATIN SMALL LETTER S WITH PALATAL HOOK]
        // ṡ  [LATIN SMALL LETTER S WITH DOT ABOVE]
        // ṣ  [LATIN SMALL LETTER S WITH DOT BELOW]
        // ṥ  [LATIN SMALL LETTER S WITH ACUTE AND DOT ABOVE]
        // ṧ  [LATIN SMALL LETTER S WITH CARON AND DOT ABOVE]
        // ṩ  [LATIN SMALL LETTER S WITH DOT BELOW AND DOT ABOVE]
        // ẜ  [LATIN SMALL LETTER LONG S WITH DIAGONAL STROKE]
        // ẝ  [LATIN SMALL LETTER LONG S WITH HIGH STROKE]
        // ⓢ  [CIRCLED LATIN SMALL LETTER S]
        // Ꞅ  [LATIN CAPITAL LETTER INSULAR S]
        // ｓ  [FULLWIDTH LATIN SMALL LETTER S]
        case '\u015B' | '\u015D' | '\u015F' | '\u0161' | '\u017F' | '\u0219' | '\u023F' | '\u0282' | '\u1D74' | '\u1D8A' | '\u1E61' | '\u1E63' | '\u1E65' | '\u1E67' | '\u1E69' | '\u1E9C' | '\u1E9D' | '\u24E2' | '\uA784' | '\uFF53' => "s"

        // ASCII: ss

        // ß  [LATIN SMALL LETTER SHARP S]
        case '\u00DF' => "ss"

        // ASCII: st

        // ﬆ  [LATIN SMALL LIGATURE ST]
        case '\uFB06' => "st"

        // ASCII: t

        // ţ  [LATIN SMALL LETTER T WITH CEDILLA]
        // ť  [LATIN SMALL LETTER T WITH CARON]
        // ŧ  [LATIN SMALL LETTER T WITH STROKE]
        // ƫ  [LATIN SMALL LETTER T WITH PALATAL HOOK]
        // ƭ  [LATIN SMALL LETTER T WITH HOOK]
        // ț  [LATIN SMALL LETTER T WITH COMMA BELOW]
        // ȶ  [LATIN SMALL LETTER T WITH CURL]
        // ʇ  [LATIN SMALL LETTER TURNED T]
        // ʈ  [LATIN SMALL LETTER T WITH RETROFLEX HOOK]
        // ᵵ  [LATIN SMALL LETTER T WITH MIDDLE TILDE]
        // ṫ  [LATIN SMALL LETTER T WITH DOT ABOVE]
        // ṭ  [LATIN SMALL LETTER T WITH DOT BELOW]
        // ṯ  [LATIN SMALL LETTER T WITH LINE BELOW]
        // ṱ  [LATIN SMALL LETTER T WITH CIRCUMFLEX BELOW]
        // ẗ  [LATIN SMALL LETTER T WITH DIAERESIS]
        // ⓣ  [CIRCLED LATIN SMALL LETTER T]
        // ⱦ  [LATIN SMALL LETTER T WITH DIAGONAL STROKE]
        // ｔ  [FULLWIDTH LATIN SMALL LETTER T]
        case '\u0163' | '\u0165' | '\u0167' | '\u01AB' | '\u01AD' | '\u021B' | '\u0236' | '\u0287' | '\u0288' | '\u1D75' | '\u1E6B' | '\u1E6D' | '\u1E6F' | '\u1E71' | '\u1E97' | '\u24E3' | '\u2C66' | '\uFF54' => "t"

        // ASCII: tc

        // ʨ  [LATIN SMALL LETTER TC DIGRAPH WITH CURL]
        case '\u02A8' => "tc"

        // ASCII: th

        // þ  [LATIN SMALL LETTER THORN]
        // ᵺ  [LATIN SMALL LETTER TH WITH STRIKETHROUGH]
        // ꝧ  [LATIN SMALL LETTER THORN WITH STROKE THROUGH DESCENDER]
        case '\u00FE' | '\u1D7A' | '\uA767' => "th"

        // ASCII: ts

        // ʦ  [LATIN SMALL LETTER TS DIGRAPH]
        case '\u02A6' => "ts"

        // ASCII: tz

        // ꜩ  [LATIN SMALL LETTER TZ]
        case '\uA729' => "tz"

        // ASCII: u

        // ù  [LATIN SMALL LETTER U WITH GRAVE]
        // ú  [LATIN SMALL LETTER U WITH ACUTE]
        // û  [LATIN SMALL LETTER U WITH CIRCUMFLEX]
        // ü  [LATIN SMALL LETTER U WITH DIAERESIS]
        // ũ  [LATIN SMALL LETTER U WITH TILDE]
        // ū  [LATIN SMALL LETTER U WITH MACRON]
        // ŭ  [LATIN SMALL LETTER U WITH BREVE]
        // ů  [LATIN SMALL LETTER U WITH RING ABOVE]
        // ű  [LATIN SMALL LETTER U WITH DOUBLE ACUTE]
        // ų  [LATIN SMALL LETTER U WITH OGONEK]
        // ư  [LATIN SMALL LETTER U WITH HORN]
        // ǔ  [LATIN SMALL LETTER U WITH CARON]
        // ǖ  [LATIN SMALL LETTER U WITH DIAERESIS AND MACRON]
        // ǘ  [LATIN SMALL LETTER U WITH DIAERESIS AND ACUTE]
        // ǚ  [LATIN SMALL LETTER U WITH DIAERESIS AND CARON]
        // ǜ  [LATIN SMALL LETTER U WITH DIAERESIS AND GRAVE]
        // ȕ  [LATIN SMALL LETTER U WITH DOUBLE GRAVE]
        // ȗ  [LATIN SMALL LETTER U WITH INVERTED BREVE]
        // ʉ  [LATIN SMALL LETTER U BAR]
        // ᵤ  [LATIN SUBSCRIPT SMALL LETTER U]
        // ᶙ  [LATIN SMALL LETTER U WITH RETROFLEX HOOK]
        // ṳ  [LATIN SMALL LETTER U WITH DIAERESIS BELOW]
        // ṵ  [LATIN SMALL LETTER U WITH TILDE BELOW]
        // ṷ  [LATIN SMALL LETTER U WITH CIRCUMFLEX BELOW]
        // ṹ  [LATIN SMALL LETTER U WITH TILDE AND ACUTE]
        // ṻ  [LATIN SMALL LETTER U WITH MACRON AND DIAERESIS]
        // ụ  [LATIN SMALL LETTER U WITH DOT BELOW]
        // ủ  [LATIN SMALL LETTER U WITH HOOK ABOVE]
        // ứ  [LATIN SMALL LETTER U WITH HORN AND ACUTE]
        // ừ  [LATIN SMALL LETTER U WITH HORN AND GRAVE]
        // ử  [LATIN SMALL LETTER U WITH HORN AND HOOK ABOVE]
        // ữ  [LATIN SMALL LETTER U WITH HORN AND TILDE]
        // ự  [LATIN SMALL LETTER U WITH HORN AND DOT BELOW]
        // ⓤ  [CIRCLED LATIN SMALL LETTER U]
        // ｕ  [FULLWIDTH LATIN SMALL LETTER U]
        case '\u00F9' | '\u00FA' | '\u00FB' | '\u00FC' | '\u0169' | '\u016B' | '\u016D' | '\u016F' | '\u0171' | '\u0173' | '\u01B0' | '\u01D4' | '\u01D6' | '\u01D8' | '\u01DA' | '\u01DC' | '\u0215' | '\u0217' | '\u0289' | '\u1D64' | '\u1D99' | '\u1E73' | '\u1E75' | '\u1E77' | '\u1E79' | '\u1E7B' | '\u1EE5' | '\u1EE7' | '\u1EE9' | '\u1EEB' | '\u1EED' | '\u1EEF' | '\u1EF1' | '\u24E4' | '\uFF55' => "u"

        // ASCII: ue

        // ᵫ  [LATIN SMALL LETTER UE]
        case '\u1D6B' => "ue"

        // ASCII: v

        // ʋ  [LATIN SMALL LETTER V WITH HOOK]
        // ʌ  [LATIN SMALL LETTER TURNED V]
        // ᵥ  [LATIN SUBSCRIPT SMALL LETTER V]
        // ᶌ  [LATIN SMALL LETTER V WITH PALATAL HOOK]
        // ṽ  [LATIN SMALL LETTER V WITH TILDE]
        // ṿ  [LATIN SMALL LETTER V WITH DOT BELOW]
        // ⓥ  [CIRCLED LATIN SMALL LETTER V]
        // ⱱ  [LATIN SMALL LETTER V WITH RIGHT HOOK]
        // ⱴ  [LATIN SMALL LETTER V WITH CURL]
        // ꝟ  [LATIN SMALL LETTER V WITH DIAGONAL STROKE]
        // ｖ  [FULLWIDTH LATIN SMALL LETTER V]
        case '\u028B' | '\u028C' | '\u1D65' | '\u1D8C' | '\u1E7D' | '\u1E7F' | '\u24E5' | '\u2C71' | '\u2C74' | '\uA75F' | '\uFF56' => "v"

        // ASCII: vy

        // ꝡ  [LATIN SMALL LETTER VY]
        case '\uA761' => "vy"

        // ASCII: w

        // ŵ  [LATIN SMALL LETTER W WITH CIRCUMFLEX]
        // ƿ  http://en.wikipedia.org/wiki/Wynn  [LATIN LETTER WYNN]
        // ʍ  [LATIN SMALL LETTER TURNED W]
        // ẁ  [LATIN SMALL LETTER W WITH GRAVE]
        // ẃ  [LATIN SMALL LETTER W WITH ACUTE]
        // ẅ  [LATIN SMALL LETTER W WITH DIAERESIS]
        // ẇ  [LATIN SMALL LETTER W WITH DOT ABOVE]
        // ẉ  [LATIN SMALL LETTER W WITH DOT BELOW]
        // ẘ  [LATIN SMALL LETTER W WITH RING ABOVE]
        // ⓦ  [CIRCLED LATIN SMALL LETTER W]
        // ⱳ  [LATIN SMALL LETTER W WITH HOOK]
        // ｗ  [FULLWIDTH LATIN SMALL LETTER W]
        case '\u0175' | '\u01BF' | '\u028D' | '\u1E81' | '\u1E83' | '\u1E85' | '\u1E87' | '\u1E89' | '\u1E98' | '\u24E6' | '\u2C73' | '\uFF57' => "w"

        // ASCII: x

        // ᶍ  [LATIN SMALL LETTER X WITH PALATAL HOOK]
        // ẋ  [LATIN SMALL LETTER X WITH DOT ABOVE]
        // ẍ  [LATIN SMALL LETTER X WITH DIAERESIS]
        // ₓ  [LATIN SUBSCRIPT SMALL LETTER X]
        // ⓧ  [CIRCLED LATIN SMALL LETTER X]
        // ｘ  [FULLWIDTH LATIN SMALL LETTER X]
        case '\u1D8D' | '\u1E8B' | '\u1E8D' | '\u2093' | '\u24E7' | '\uFF58' => "x"

        // ASCII: y

        // ý  [LATIN SMALL LETTER Y WITH ACUTE]
        // ÿ  [LATIN SMALL LETTER Y WITH DIAERESIS]
        // ŷ  [LATIN SMALL LETTER Y WITH CIRCUMFLEX]
        // ƴ  [LATIN SMALL LETTER Y WITH HOOK]
        // ȳ  [LATIN SMALL LETTER Y WITH MACRON]
        // ɏ  [LATIN SMALL LETTER Y WITH STROKE]
        // ʎ  [LATIN SMALL LETTER TURNED Y]
        // ẏ  [LATIN SMALL LETTER Y WITH DOT ABOVE]
        // ẙ  [LATIN SMALL LETTER Y WITH RING ABOVE]
        // ỳ  [LATIN SMALL LETTER Y WITH GRAVE]
        // ỵ  [LATIN SMALL LETTER Y WITH DOT BELOW]
        // ỷ  [LATIN SMALL LETTER Y WITH HOOK ABOVE]
        // ỹ  [LATIN SMALL LETTER Y WITH TILDE]
        // ỿ  [LATIN SMALL LETTER Y WITH LOOP]
        // ⓨ  [CIRCLED LATIN SMALL LETTER Y]
        // ｙ  [FULLWIDTH LATIN SMALL LETTER Y]
        case '\u00FD' | '\u00FF' | '\u0177' | '\u01B4' | '\u0233' | '\u024F' | '\u028E' | '\u1E8F' | '\u1E99' | '\u1EF3' | '\u1EF5' | '\u1EF7' | '\u1EF9' | '\u1EFF' | '\u24E8' | '\uFF59' => "y"

        // ASCII: z

        // ź  [LATIN SMALL LETTER Z WITH ACUTE]
        // ż  [LATIN SMALL LETTER Z WITH DOT ABOVE]
        // ž  [LATIN SMALL LETTER Z WITH CARON]
        // ƶ  [LATIN SMALL LETTER Z WITH STROKE]
        // ȝ  http://en.wikipedia.org/wiki/Yogh  [LATIN SMALL LETTER YOGH]
        // ȥ  [LATIN SMALL LETTER Z WITH HOOK]
        // ɀ  [LATIN SMALL LETTER Z WITH SWASH TAIL]
        // ʐ  [LATIN SMALL LETTER Z WITH RETROFLEX HOOK]
        // ʑ  [LATIN SMALL LETTER Z WITH CURL]
        // ᵶ  [LATIN SMALL LETTER Z WITH MIDDLE TILDE]
        // ᶎ  [LATIN SMALL LETTER Z WITH PALATAL HOOK]
        // ẑ  [LATIN SMALL LETTER Z WITH CIRCUMFLEX]
        // ẓ  [LATIN SMALL LETTER Z WITH DOT BELOW]
        // ẕ  [LATIN SMALL LETTER Z WITH LINE BELOW]
        // ⓩ  [CIRCLED LATIN SMALL LETTER Z]
        // ⱬ  [LATIN SMALL LETTER Z WITH DESCENDER]
        // ꝣ  [LATIN SMALL LETTER VISIGOTHIC Z]
        // ｚ  [FULLWIDTH LATIN SMALL LETTER Z]
        case '\u017A' | '\u017C' | '\u017E' | '\u01B6' | '\u021D' | '\u0225' | '\u0240' | '\u0290' | '\u0291' | '\u1D76' | '\u1D8E' | '\u1E91' | '\u1E93' | '\u1E95' | '\u24E9' | '\u2C6C' | '\uA763' | '\uFF5A' => "z"

        // ASCII: {

        // ❴  [MEDIUM LEFT CURLY BRACKET ORNAMENT]
        // ｛  [FULLWIDTH LEFT CURLY BRACKET]
        case '\u2774' | '\uFF5B' => "{"

        // ASCII: }

        // ❵  [MEDIUM RIGHT CURLY BRACKET ORNAMENT]
        // ｝  [FULLWIDTH RIGHT CURLY BRACKET]
        case '\u2775' | '\uFF5D' => "}"

        case _ => null // avoid the String memory allocation from calling c.toString
      }
    }
  }

}


