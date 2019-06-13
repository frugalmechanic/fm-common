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

import fm.common.{Normalize, OptionCache, ThreadLocalHashMap}
import java.io.File
import java.math.{BigDecimal, BigInteger}
import java.text.{DecimalFormat, NumberFormat, ParseException}
import java.util.Locale
import scala.annotation.switch
import scala.util.matching.Regex

object RichString {
  def parseBoolean(s: String): Option[Boolean] = {
    if (null == s) return None

    val lower: String = s.trim.toLowerCase
    if (lower == "") return None

    lower match {
      case "true" | "t" | "yes" | "y" | "1" => OptionCache.valueOf(true)
      case "false" | "f" | "no" | "n" | "0" => OptionCache.valueOf(false)
    }
  }

  // NumberFormat.getInstance(locale) is expensive (and not thread-safe) so we need to cache it
  private object BigDecimalFormatCache extends ThreadLocalHashMap[Locale,DecimalFormat] {
    override protected def initialValue(locale: Locale): Option[DecimalFormat] = {
      val bigDecimalFormat: DecimalFormat = NumberFormat.getInstance(locale).asInstanceOf[DecimalFormat]
      bigDecimalFormat.setParseBigDecimal(true)
      Some(bigDecimalFormat)
    }
  }
}

final class RichString(val s: String) extends AnyVal {
  /**
   * Same as String.intern but safe for use when the string is null (i.e. it just returns null)
   */
  def internOrNull: String = if (null == s) null else s.intern
  
  /**
   * If the string is blank returns None else Some(string)
   */
  def toBlankOption: Option[String] = if (new RichCharSequence(s).isNotNullOrBlank) Some(s) else None
  
  /**
   * If this string starts with the lead param then return a new string with lead stripped from the start
   * 
   * NOTE: The same functionality is available in Scala's StringOps.stripPrefix
   */
  def stripLeading(lead: String): String = if (s.startsWith(lead)) s.substring(lead.length) else s
  
  /**
   * If this string ends with the trail param then return a new string with trail stripped from the end
   * 
   * NOTE: The same functionality is available in Scala's StringOps.stripSuffix
   */
  def stripTrailing(trail: String): String = if (s.endsWith(trail)) s.substring(0, s.length - trail.length) else s
  
  /**
   * If this string does not start with the lead param then return a new string with it added to the start of the string
   * 
   * TODO: is there a better name for this?
   */
  def requireLeading(lead: String): String = if (s.startsWith(lead)) s else lead+s
  
  /**
   * If this string does not ends with the trail param then return a new string with it added to the end of the string
   * 
   * TODO: is there a better name for this?
   */
  def requireTrailing(trail: String): String = if (s.endsWith(trail)) s else s+trail
  
  def toBooleanOption: Option[Boolean] = try { OptionCache.valueOf(java.lang.Boolean.valueOf(s)) } catch { case _: NumberFormatException => None }
  def toByteOption:    Option[Byte]    = try { OptionCache.valueOf(java.lang.Byte.valueOf(s))    } catch { case _: NumberFormatException => None }
  def toShortOption:   Option[Short]   = try { OptionCache.valueOf(java.lang.Short.valueOf(s))   } catch { case _: NumberFormatException => None }
  def toIntOption:     Option[Int]     = try { OptionCache.valueOf(java.lang.Integer.valueOf(s)) } catch { case _: NumberFormatException => None }
  def toLongOption:    Option[Long]    = try { OptionCache.valueOf(java.lang.Long.valueOf(s))    } catch { case _: NumberFormatException => None }
  def toFloatOption:   Option[Float]   = try { Some(java.lang.Float.valueOf(s))   } catch { case _: NumberFormatException => None }
  def toDoubleOption:  Option[Double]  = try { Some(java.lang.Double.valueOf(s))  } catch { case _: NumberFormatException => None }
  
  def isBoolean: Boolean = toBooleanOption.isDefined
  def isByte:    Boolean = toByteOption.isDefined
  def isShort:   Boolean = toShortOption.isDefined
  def isInt:     Boolean = toIntOption.isDefined
  def isLong:    Boolean = toLongOption.isDefined
  def isFloat:   Boolean = toFloatOption.isDefined
  def isDouble:  Boolean = toDoubleOption.isDefined
  
  def toBigDecimalOption: Option[BigDecimal] = {
    try {
      if (s == null) None
      else Some(new BigDecimal(s))
    } catch {
      case ex: NumberFormatException => None
    }
  }
  
  def toBigDecimal: BigDecimal = toBigDecimalOption.getOrElse{ throw new NumberFormatException(s"RichString.toBigDecimal parsing error for value: $s") }
  def isBigDecimal: Boolean = toBigDecimalOption.isDefined
  def isNotBigDecimal: Boolean = !isBigDecimal

  def toBigIntegerOption: Option[BigInteger] = toBigDecimalOption.flatMap{ bd =>
    try {
      Some(bd.toBigIntegerExact())
    } catch {
      case ex: ArithmeticException => None
    }
  }
  
  def toBigInteger: BigInteger = toBigIntegerOption.getOrElse{ throw new NumberFormatException(s"RichString.toBigInteger parsing error on value: $s") }

  def parseBigDecimal(implicit locale: Locale): Option[BigDecimal] = if (null == s) None else try {
    val res: BigDecimal = RichString.BigDecimalFormatCache(locale).parse(s).asInstanceOf[BigDecimal]
    Some(res)
  } catch {
    case _: ParseException => None
  }
  
  /**
   * Unlike toBoolean/toBooleanOption/isBoolean this method will
   * attempt to parse a boolean value from a string.
   */
  def parseBoolean: Option[Boolean] = RichString.parseBoolean(s)
  
  /** A shortcut for "new java.io.File(s)" */
  def toFile: File = new File(s)
  
  /**
   * Truncate the string to length if it is currently larger than length.
   * 
   * Note: The resulting string will not be longer than length.  (i.e the omission counts towards the length)
   * 
   * @param length The length to truncate the string to
   * @param omission If the string is truncated then add this to the end (Note: The resulting still will be at most length)
   */
  def truncate(length: Int, omission: String = ""): String = {
    if (s.length > length) s.substring(0, length-omission.length)+omission else s
  }
  
  /** See fm.common.Normalize.lowerAlphaNumeric */
  def lowerAlphaNumeric: String = Normalize.lowerAlphanumeric(s)
  
  /** See fm.common.Normalize.lowerAlphaNumericWords */
  def lowerAlphaNumericWords: Array[String] = Normalize.lowerAlphaNumericWords(s)
  
  /** See fm.common.Normalize.name */
  def urlName: String = Normalize.urlName(s)
  
  /** See org.apache.commons.lang3.text.WordUtils.capitalize */
  def capitalizeWords: String = capitalizeWords(null:_*)
  
  /** See org.apache.commons.lang3.text.WordUtils.capitalize */
  def capitalizeWords(delimiters: Char*): String = {
    val delimLen: Int = if (delimiters == null) -1 else delimiters.length
    
    if (s == null || s.length == 0 || delimLen == 0) return s
    
    val buffer: Array[Char] = s.toCharArray()
    var capitalizeNext: Boolean = true
    
    var i: Int = 0
    while (i < buffer.length) {
      val ch: Char = buffer(i)
      if (isDelimiter(ch, delimiters)) {
        capitalizeNext = true
      } else if (capitalizeNext) {
        buffer(i) = ch.toUpper //Character.toTitleCase(ch)
        capitalizeNext = false
      }
      
      i += 1
    }
    
    new String(buffer)
  }
  
  /** See org.apache.commons.lang3.text.WordUtils.capitalizeFully */
  def capitalizeFully: String = capitalizeFully(null:_*)
  
  /** See org.apache.commons.lang3.text.WordUtils.capitalizeFully */
  def capitalizeFully(delimiters: Char*): String = {
    val delimLen: Int = if (delimiters == null) -1 else delimiters.length

    if (s == null || s.length() == 0 || delimLen == 0) return s

    val lower: String = s.toLowerCase()
    new RichString(lower).capitalizeWords(delimiters:_*)
  }
  
  /** See org.apache.commons.lang3.text.WordUtils.isDelimiter */
  private def isDelimiter(ch: Char, delimiters: Seq[Char]): Boolean = {
    if (delimiters == null) Character.isWhitespace(ch)
    else delimiters.exists{ delim: Char => delim == ch }
  }
  
  def pad(length: Int, c: Char = ' '): String = rPad(length, c)

  def lPad(length: Int, c: Char = ' '): String = {
    val target: Int = length - s.length
    if (target <= 0) s else repeat(c, target)+s
  }

  def rPad(length: Int, c: Char = ' '): String = {
    val target: Int = length - s.length
    if (target <= 0) s else s+repeat(c, target)
  }
  
  private def repeat(c: Char, times: Int): String = {
    (times: @switch) match {
      case 0 => ""
      case 1 => String.valueOf(c)
      case _ =>
        val arr = new Array[Char](times)
        java.util.Arrays.fill(arr, c)
        new String(arr)
    }
  }
  
  def replaceAll(regex: Regex, replacement: String): String = regex.replaceAllIn(s, replacement)
  
  def replaceFirst(regex: Regex, replacement: String): String = regex.replaceFirstIn(s, replacement)
  
  def stripAccents: String = Normalize.stripAccents(s)

  def toCodePointArray: Array[Int] = {
    if (null == s) throw new NullPointerException()

    val arr: Array[Int] = new Array(s.codePointCount(0, s.length))

    var arrIdx: Int = 0
    var strIdx: Int = 0
    val len: Int = s.length

    while(strIdx < len) {
      val ch: Char = s.charAt(strIdx)

      if (Character.isHighSurrogate(ch)) {
        if (strIdx + 1 < len && Character.isLowSurrogate(s.charAt(strIdx + 1))) {
          arr(arrIdx) = Character.toCodePoint(ch, s.charAt(strIdx + 1))
          strIdx += 1
        } else {
          // Output the un-paired high surrogate as-is
          arr(arrIdx) = ch
        }
      } else {
        // Output as-is (includes un-paired low surrogates)
        arr(arrIdx) = ch
      }

      strIdx += 1
      arrIdx += 1
    }

    arr
  }

  
//  /**
//   * The plural form of the string
//   */
//  def plural: String = org.atteo.evo.inflector.English.plural(s)
}