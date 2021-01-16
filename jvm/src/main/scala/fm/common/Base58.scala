/*
 * This was originally from: https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Base58.java
 *
 * Copyright 2011 Google Inc.
 * Copyright 2018 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fm.common

import java.nio.CharBuffer
import java.util.Arrays

/**
 * Base58 is a way to encode Bitcoin addresses (or arbitrary data) as alphanumeric strings.
 * <p>
 * Note that this is not the same base58 as used by Flickr, which you may find referenced around the Internet.
 * <p>
 * You may want to consider working with {@link PrefixedChecksummedBytes} instead, which
 * adds support for testing the prefix and suffix bytes commonly found in addresses.
 * <p>
 * Satoshi explains: why base-58 instead of standard base-64 encoding?
 * <ul>
 * <li>Don't want 0OIl characters that look the same in some fonts and
 *     could be used to create visually identical looking account numbers.</li>
 * <li>A string with non-alphanumeric characters is not as easily accepted as an account number.</li>
 * <li>E-mail usually won't line-break if there's no punctuation to break at.</li>
 * <li>Doubleclicking selects the whole number as one word if it's all alphanumeric.</li>
 * </ul>
 * <p>
 * However, note that the encoding/decoding runs in O(n&sup2;) time, so it is not useful for large data.
 * <p>
 * The basic idea of the encoding is to treat the data bytes as a large number represented using
 * base-256 digits, convert the number to be represented using base-58 digits, preserve the exact
 * number of leading zeros (which are otherwise lost during the mathematical operations on the
 * numbers), and finally represent the resulting base-58 digits as alphanumeric ASCII characters.
 */
object Base58 extends BaseEncoding {
  private val ALPHABET: Array[Char] = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray
  private val ENCODED_ZERO: Char = ALPHABET(0)

  private val INDEXES: Array[Int] = {
    val arr: Array[Int] = new Array(128)

    Arrays.fill(arr, -1)

    var i: Int = 0

    while (i < ALPHABET.length) {
      arr(ALPHABET(i)) = i
      i += 1
    }

    arr
  }

  def main(args: Array[String]): Unit = {
    def go(iter: Int): String = {
      var res: Long = 0
      var i: Int = 0
      while (i < iter) {
        val base58: String = UUID().toBase58()
        res += Base58.decode(base58).length
        //res += UUID().toBase58.length
        i += 1
      }

      res.toString
    }

    def benchmark(iter: Int): Unit = {
      val (time: Long, res: String) = Util.time{ go(iter) }

      println(s"ENCODING - Res: $res  Time (ms): $time  ops/ms: ${iter.toDouble/time.toDouble}  ms/op: ${time.toDouble/iter.toDouble}")
    }

    // Warmup
    go(1000000)

    // Actual run
    benchmark(1000000)
  }

  def encode(bytes: Array[Byte], offset: Int, length: Int): String = {
    encodeImpl(Arrays.copyOfRange(bytes, offset, offset + length))
  }

  /**
   * Encodes the given bytes as a base58 string (no checksum is appended).
   *
   * @param input the bytes to encode
   * @return the base58-encoded string
   */
  def encode(input: Array[Byte]): String = {
    encodeImpl(Arrays.copyOf(input, input.length))
  }

  private def encodeImpl(input: Array[Byte]): String = {
    if (input.length == 0) return ""

    // Count leading zeros.
    var zeros: Int = 0

    while (zeros < input.length && input(zeros) == 0) {
      zeros += 1
    }

    // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
    val encoded: Array[Char] = new Array(input.length * 2) // upper bound
    var outputStart: Int = encoded.length

    var inputStart: Int = zeros

    while (inputStart < input.length) {
      outputStart -= 1
      encoded(outputStart) = ALPHABET(divmodForEncode(input, inputStart))
      if (input(inputStart) == 0) inputStart += 1
    }

    // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
    while (outputStart < encoded.length && encoded(outputStart) == ENCODED_ZERO) {
      outputStart += 1
    }

    zeros -= 1

    while (zeros >= 0) {
      outputStart -= 1
      encoded(outputStart) = ENCODED_ZERO
      zeros -= 1
    }

    // Return encoded string (including encoded leading zeros).
    new String(encoded, outputStart, encoded.length - outputStart)
  }

  /**
   * Encodes the given version and bytes as a base58 string. A checksum is appended.
   *
   * @param version the version to encode
   * @param payload the bytes to encode, e.g. pubkey hash
   * @return the base58-encoded string
   */
  def encodeChecked(version: Int, payload: Array[Byte]): String = {
    if (version < 0 || version > 255) throw new IllegalArgumentException("Version not in range.")

    // A stringified buffer is:
    // 1 byte version + data bytes + 4 bytes check code (a truncated hash)
    val addressBytes: Array[Byte] = new Array(1 + payload.length + 4)
    addressBytes(0) = version.toByte
    System.arraycopy(payload, 0, addressBytes, 1, payload.length)
    val checksum: Array[Byte] = hashTwice(addressBytes, 0, payload.length + 1)
    System.arraycopy(checksum, 0, addressBytes, payload.length + 1, 4)
    encode(addressBytes)
  }

  /**
   * Encodes the given version and bytes as a base58 string. A checksum is appended.
   *
   * Note: This assumes you have already pre-pended the version byte (if applicable)
   *
   * @param payload the bytes to encode, e.g. pubkey hash
   * @return the base58-encoded string
   */
  def encodeChecked(payload: Array[Byte]): String = {
    val addressBytes: Array[Byte] = Arrays.copyOf(payload, payload.length + 4)
    val checksum: Array[Byte] = hashTwice(addressBytes, 0, payload.length)
    System.arraycopy(checksum, 0, addressBytes, payload.length, 4)
    encode(addressBytes)
  }

  def decode(input: Array[Char]): Array[Byte] = {
    decode(CharBuffer.wrap(input))
  }

  /**
   * Decodes the given base58 string into the original data bytes.
   *
   * @param input the base58-encoded string to decode
   * @return the decoded data bytes
   */
  def decode(input: CharSequence): Array[Byte] = {
    if (input.length() == 0) return new Array(0)

    // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
    val input58: Array[Byte] = new Array(input.length())

    var i: Int = 0

    while (i < input.length()) {
      val c: Char = input.charAt(i)
      val digit: Int = if (c < 128) INDEXES(c) else -1
      if (digit < 0) throw new IllegalArgumentException("Invalid Character: "+c+" at idx: "+i)
      input58(i) = digit.toByte

      i += 1
    }
    // Count leading zeros.
    var zeros: Int = 0
    while (zeros < input58.length && input58(zeros) == 0) {
      zeros += 1
    }

    // Convert base-58 digits to base-256 digits.
    val decoded: Array[Byte] = new Array(input.length())
    var outputStart: Int = decoded.length

    var inputStart: Int = zeros

    while (inputStart < input58.length) {
      outputStart -= 1
      decoded(outputStart) = divmodForDecode(input58, inputStart)
      if (input58(inputStart) == 0) inputStart += 1 // optimization - skip leading zeros
    }
    // Ignore extra leading zeroes that were added during the calculation.
    while (outputStart < decoded.length && decoded(outputStart) == 0) {
      outputStart += 1
    }

    // Return decoded data (including original number of leading zeros).
    Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length)
  }

  /**
   * Decodes the given base58 string into the original data bytes, using the checksum in the
   * last 4 bytes of the decoded data to verify that the rest are correct. The checksum is
   * removed from the returned data.
   *
   * @param input the base58-encoded string to decode (which should include the checksum)
   */
  def decodeChecked(input: String): Array[Byte] = {
    val decoded: Array[Byte] = decode(input)
    if (decoded.length < 4) throw new IllegalArgumentException("InvalidDataLength - Input too short: " + decoded.length)
    val data: Array[Byte] = Arrays.copyOfRange(decoded, 0, decoded.length - 4)
    val checksum: Array[Byte] = Arrays.copyOfRange(decoded, decoded.length - 4, decoded.length)
    val actualChecksum: Array[Byte] = Arrays.copyOfRange(hashTwice(data), 0, 4)
    if (!Arrays.equals(checksum, actualChecksum)) throw new IllegalArgumentException("Invalid Checksum")
    data
  }

  /**
   * See Commented out JavaDocs for the original divmod method.
   *
   * This has hardcoded values of base=256 and divisor=58 which almost doubles performance of the method
   */
  private def divmodForEncode(number: Array[Byte], firstDigit: Int): Byte = {
    // this is just long division which accounts for the base of the input digits
    var remainder: Int = 0

    var i: Int = firstDigit

    while (i < number.length) {
      val digit: Int = number(i) & 0xFF
      val temp: Int = remainder * 256 + digit
      number(i) = (temp / 58).toByte
      remainder = temp % 58
      i += 1
    }

    remainder.toByte
  }

  /**
   * See Commented out JavaDocs for the original divmod method.
   *
   * This has hardcoded values of base=58 and divisor=256 which almost doubles performance of the method
   */
  private def divmodForDecode(number: Array[Byte], firstDigit: Int): Byte = {
    // this is just long division which accounts for the base of the input digits
    var remainder: Int = 0

    var i: Int = firstDigit

    while (i < number.length) {
      val digit: Int = number(i) & 0xFF
      val temp: Int = remainder * 58 + digit
      number(i) = (temp / 256).toByte
      remainder = temp % 256
      i += 1
    }

    remainder.toByte
  }

//  /**
//   * Divides a number, represented as an array of bytes each containing a single digit
//   * in the specified base, by the given divisor. The given number is modified in-place
//   * to contain the quotient, and the return value is the remainder.
//   *
//   * @param number the number to divide
//   * @param firstDigit the index within the array of the first non-zero digit
//   *        (this is used for optimization by skipping the leading zeros)
//   * @param base the base in which the number's digits are represented (up to 256)
//   * @param divisor the number to divide by (up to 256)
//   * @return the remainder of the division operation
//   */
//  private def divmod(number: Array[Byte], firstDigit: Int, base: Int, divisor: Int): Byte = {
//    // this is just long division which accounts for the base of the input digits
//    var remainder: Int = 0
//
//    var i: Int = firstDigit
//
//    while (i < number.length) {
//      val digit: Int = number(i) & 0xFF
//      val temp: Int = remainder * base + digit
//      number(i) = (temp / divisor).toByte
//      remainder = temp % divisor
//      i += 1
//    }
//
//    remainder.toByte
//  }

  private def hashTwice(input: Array[Byte], offset: Int, length: Int): Array[Byte] = {
    hashTwice(Arrays.copyOfRange(input, offset, offset + length))
  }

  private def hashTwice(input: Array[Byte]): Array[Byte] = {
    DigestUtils.sha256(DigestUtils.sha256(input))
  }
}
