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

// Was playing around these parameters for encoding any non alphanumeric ASCII in addition to any non-ASCII Unicode
//
//object PartIdCode extends Bootstring(
//  alphabet = (('a' to 'z') ++ ('0' to '9')).mkString(""),
//  tmin = 1,
//  tmax = 26,
//  skew = 38,
//  damp = 700,
//  initialBias = 72,
//  initialN = 0, // 0 since we encode any non AlphaNumeric ASCII chars (Note: this is 127 for Punycode)
//  delimiter = '-',
//) {
//  def isBasicCodePoint(codepoint: Int): Boolean = codepoint < 127 && Character.isLetterOrDigit(codepoint)
//}

/**
 * Bootstring encoding as defined in From: https://tools.ietf.org/html/rfc3492
 */
abstract class Bootstring(
  alphabet: String,
  tmin: Int,
  tmax: Int,
  skew: Int,
  damp: Int,
  initialBias: Int,
  initialN: Int,
  delimiter: Char
) {
  val base: Int = alphabet.length

  private val EmptyPair: (String,String) = ("","")

  // Conditions that should hold true according to rfc3492
  require(0 <= tmin && tmin <= tmax && tmax <= base - 1, "Expected: 0 <= tmin <= tmax <= base-1")
  require(skew >= 1, "Expected: skeq >= 1")
  require(damp >= 2, "Expected: damp >= 2")
  require(initialBias % base <= base - tmin, "Expected: initial_bias mod base <= base - tmin")

  private def ASCIIMax: Int = 127

  require(alphabet.forall{ ch: Char => ch <= ASCIIMax }, "Expected ASCII Alphabet")
  require(alphabet.forall{ ch: Char => !ch.isControl }, "Expected non-control alphabet characters")

  private val isMixedCase: Boolean = {
    val hasUpper: Boolean = alphabet.exists{ ch: Char => ch.isLetter && ch.isUpper }
    val hasLower: Boolean = alphabet.exists{ ch: Char => ch.isLetter && ch.isLower }
    hasUpper && hasLower
  }

  // We will use these for the encode digit method
  private val lowerDigitEncoder: ImmutableArray[Char] = ImmutableArray.wrap((if (isMixedCase) alphabet else alphabet.toLowerCase).toCharArray)
  private val upperDigitEncoder: ImmutableArray[Char] = if (isMixedCase) lowerDigitEncoder else ImmutableArray.wrap(alphabet.toUpperCase.toCharArray)

  /**
   * Maps ASCII Characters to their corresponding digit (or -1 if they do not have one)
   */
  private val digitDecoder: ImmutableArray[Int] = {
    val arr: Array[Int] = new Array(ASCIIMax + 1)
    java.util.Arrays.fill(arr, -1)

    var i: Int = 0
    while (i < lowerDigitEncoder.length) {
      val ch: Char = lowerDigitEncoder(i)

      if (isMixedCase) {
        arr(ch) = i
      } else {
        arr(ch.toUpper) = i
        arr(ch.toLower) = i
      }

      i += 1
    }

    ImmutableArray.wrap(arr)
  }

  /**
   * Is this a Basic Code Point that should not be encoded and just passed through?
   */
  def isBasicCodePoint(codepoint: Int): Boolean

  final def maxint: Int = Int.MaxValue

  final def isDelimiter(codepoint: Int): Boolean = codepoint === delimiter.toInt

  final def decodeDigit(codepoint: Int): Int = {
    if (codepoint < 0 || codepoint > ASCIIMax) throw new IllegalArgumentException("Invalid digit for decoding: "+codepoint)
    val res: Int = digitDecoder(codepoint)
    if (res === -1) throw new IllegalArgumentException("Invalid digit for decoding: "+codepoint)
    res
  }

  final def encodeDigit(digit: Int, uppercase: Boolean): Char = {
    if (digit < 0 || digit > base) throw new IllegalArgumentException(s"Expected digit ($digit) to be >= 0 and <= base ($base)")
    if (uppercase) upperDigitEncoder(digit) else lowerDigitEncoder(digit)
  }

  final def isBasicUpperCase(codepoint: Int): Boolean = codepoint < 128 && isBasicUpperCase(codepoint.toChar)
  final def isBasicUpperCase(ch: Char): Boolean = 'A' <= ch && ch <= 'Z'

  final def encodeBasic(codepoint: Int, uppercase: Boolean): Char = {
    if (!isBasicCodePoint(codepoint)) codepoint.toChar
    else if (uppercase) Character.toUpperCase(codepoint).toChar
    else Character.toLowerCase(codepoint).toChar
  }

  final def adapt(delta: Int, numpoints: Int, firstTime: Boolean): Int = {
    var d: Int = if (firstTime) delta / damp else delta / 2

    d += d / numpoints

    var k: Int = 0

    while (d > ((base - tmin) * tmax) / 2) {
      d /= base - tmin
      k += base
    }

    k + (base - tmin + 1) * d / (d + skew)
  }

  final def encode(input: String): String = {
    formatPair(encodeToPair(input))
  }

  final def formatPair(pair: (String, String)): String = {
    val basicChars: String = pair._1
    val encodedChars: String = pair._2

    formatPair(basicChars, encodedChars)
  }

  final def formatPair(basicChars: String, encodedChars: String): String = {
    if (basicChars.length === 0) encodedChars else basicChars+delimiter+encodedChars
  }

  final def encodeToPair(input: String): (String,String) = {
    if (null == input || input.length === 0) return EmptyPair

    var basicChars: JavaStringBuilder = null

    var j: Int = 0

    // This should count the total number of code points in our string (which could be less than the total
    // number of characters if there are any supplementary characters)
    var numberOfCodePointsInInput: Int = 0

    /* Handle the basic code points */
    while (j < input.length) {
      val ch: Char = input.charAt(j)

      if (isBasicCodePoint(ch)) {
        if (null != basicChars) basicChars.append(encodeBasic(ch, isBasicUpperCase(ch)))
      } else if (null == basicChars) {
        // Lazy initialize the StringBuilder only if we have any non-basic chars
        basicChars = new JavaStringBuilder(j)
        basicChars.append(input, 0, j)
      }

      // This should count the total number of code points in our string (which could be less than the total
      // number of characters if there are any supplementary characters)
      if (!Character.isLowSurrogate(ch)) numberOfCodePointsInInput += 1

      j += 1
    }

    // Shortcut - No non-basic chars so just return the original string
    if (null == basicChars) return (input, "")

    val encodedChars: JavaStringBuilder = new JavaStringBuilder()

    var n: Int = initialN
    var delta: Int = 0
    var bias: Int = initialBias

    var codePointsHandled: Int = basicChars.length
    val basicCodePoints: Int = basicChars.length

    /* Main encoding loop */
    while (codePointsHandled < numberOfCodePointsInInput) {
      var m: Int = maxint
      j = 0
      while (j < input.length) {
        val ch: Int = {
          val firstChar: Char = input.charAt(j)

          // Supplementary character handling
          if (Character.isSurrogate(firstChar)) {
            require(Character.isHighSurrogate(firstChar), "Expected isHighSurrogate to be true for: "+firstChar.toInt)
            j += 1
            val secondChar: Char = input.charAt(j)
            require(Character.isLowSurrogate(secondChar), "Expected isLowSurrogate to be true for: "+secondChar.toInt+" firstChar: "+firstChar.toInt+"  secondChar: "+secondChar.toInt)
            Character.toCodePoint(firstChar, secondChar)
          } else {
            firstChar.toInt
          }
        }

        if (!isBasicCodePoint(ch)) {
          if (ch >= n && ch < m) m = ch
        }

        j += 1
      }

      if (m - n > (maxint - delta) / (codePointsHandled + 1)) throw new Exception("Overflow")
      delta += (m - n) * (codePointsHandled + 1)
      n = m

      j = 0
      while (j < input.length) {
        val ch: Int = {
          val firstChar: Char = input.charAt(j)

          // Supplementary character handling
          if (Character.isSurrogate(firstChar)) {
            require(Character.isHighSurrogate(firstChar), "Expected isHighSurrogate to be true for: "+firstChar.toInt)
            j += 1
            val secondChar: Char = input.charAt(j)
            require(Character.isLowSurrogate(secondChar), "Expected isLowSurrogate to be true for: "+secondChar.toInt+" firstChar: "+firstChar.toInt+"  secondChar: "+secondChar.toInt)
            Character.toCodePoint(firstChar, secondChar)
          } else {
            firstChar.toInt
          }
        }

        if (ch < n || isBasicCodePoint(ch)) {
          delta += 1
          if (delta === 0) throw new Exception("Overflow")
        }

        if (ch == n) {
          var q: Int = delta
          var k: Int = base
          var done: Boolean = false

          while (!done) {
            val t: Int =
              if (k <= bias + tmin) tmin
              else if (k >= bias + tmax) tmax
              else k - bias

            if (q < t) {
              done = true
            } else {
              encodedChars.append(encodeDigit(t + (q - t) % (base - t), false))
              q = (q - t) / (base - t)
              k += base
            }
          }

          val isFirstTime: Boolean = codePointsHandled === basicCodePoints

          encodedChars.append(encodeDigit(q, isBasicUpperCase(ch)))
          bias = adapt(delta, codePointsHandled + 1, isFirstTime)
          delta = 0
          codePointsHandled += 1
        }

        j += 1
      }

      delta += 1
      n += 1
    }

    (basicChars.toString, encodedChars.toString)
  }

  final def decode(input: String): String = {
    var b: Int = 0
    var j: Int = 0

    // Find the last occurrence of the delimiter
    while (j < input.length) {
      val ch: Char = input.charAt(j)
      if (isDelimiter(ch)) b = j
      j += 1
    }

    val basicChars: String = input.substring(0, b)
    val encoding: String = input.substring(if (b === 0) b else b + 1, input.length())

    decodePair(basicChars, encoding)
  }

  final def decodePair(pair: (String, String)): String = {
    decodePair(pair._1, pair._2)
  }

  final def decodePair(basicChars: String, encodedChars: String): String = {
    if (encodedChars.length() === 0) return basicChars.toString

    var n: Int = initialN
    var i: Int = 0
    var bias: Int = initialBias

    // This should be larger than we need
    val output: JavaStringBuilder = new JavaStringBuilder(basicChars.length + encodedChars.length)

    // Append all the basic characters
    output.append(basicChars)

    // We need to count the number of codepoints in the output (which could be less than the number of characters
    // if we are dealing with supplementary characters)
    var codePointsInOutput: Int = basicChars.length

    var in: Int = 0

    while (in < encodedChars.length) {
      val oldI: Int = i
      var w: Int = 1
      var k: Int = base
      var done: Boolean = false

      while (!done) {
        if (in >= encodedChars.length) throw new IllegalArgumentException("Bad input")
        val digit: Int = decodeDigit(encodedChars.charAt(in))
        in += 1
        if (digit >= base) throw new IllegalArgumentException("Bad input")
        if (digit > (maxint - i) / w) throw new Exception("overflow")
        i += digit * w

        val t: Int =
          if (k <= bias + tmin) tmin
          else if (k >= bias + tmax) tmax
          else k - bias

        if (digit < t) {
          done = true
        } else {
          if (w > maxint / (base - t)) throw new Exception("overflow")
          w *= (base - t)
          k += base
        }
      }

      bias = adapt(i - oldI, codePointsInOutput + 1, oldI === 0)

      if (i / (codePointsInOutput + 1) > maxint - n) throw new Exception("overflow")
      n += i / (codePointsInOutput + 1)
      i %= (codePointsInOutput + 1)

      if (isBasicCodePoint(n)) throw new IllegalArgumentException("Bad input")

      // If there are any supplementary characters in the output then that throws off our insert index so we need to
      // calculate the actual insert index based on the number of codepoints in the output (and not characters)
      val adjustedInsertIdx: Int = output.offsetByCodePoints(0, i)

      // supplementary character handling (i.e. a single codepoint should be expanded into 2 Java Characters)
      if (Character.isSupplementaryCodePoint(n)) {
        output.insert(adjustedInsertIdx, Character.lowSurrogate(n))
        output.insert(adjustedInsertIdx, Character.highSurrogate(n))
      } else {
        output.insert(adjustedInsertIdx, n.toChar)
      }

      codePointsInOutput += 1
      i += 1
    }

    output.toString
  }

}
