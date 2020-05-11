/*
 * Copyright 2018 Frugal Mechanic (http://frugalmechanic.com)
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

import java.nio.charset.StandardCharsets.UTF_8
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestBase58 extends AnyFunSuite with Matchers {
  private implicit def stringToBytes(s: String): Array[Byte] = s.getBytes(UTF_8)
  private implicit def intToByte(i: Int): Byte = i.toByte

  private[this] val data: Vector[(Array[Byte],String)] = Vector(
    ("", ""),
    ("Hello World", "JxF12TrwUP45BMd"),
    (Array[Byte](0x00, 0x00, 0x00, 0x01), "1112"),
    (Array[Byte](0x61), "2g"),
    (Array[Byte](0x62, 0x62, 0x62), "a3gV"),
    (Array[Byte](0x63, 0x63, 0x63), "aPEr"),
    (Array[Byte](0x73, 0x69, 0x6d, 0x70, 0x6c, 0x79, 0x20, 0x61, 0x20, 0x6c, 0x6f, 0x6e, 0x67, 0x20, 0x73, 0x74, 0x72, 0x69, 0x6e, 0x67), "2cFupjhnEsSn59qHXstmK2ffpLv2"),
    (Array[Byte](0x00, 0xeb, 0x15, 0x23, 0x1d, 0xfc, 0xeb, 0x60, 0x92, 0x58, 0x86, 0xb6, 0x7d, 0x06, 0x52, 0x99, 0x92, 0x59, 0x15, 0xae, 0xb1, 0x72, 0xc0, 0x66, 0x47), "1NS17iag9jJgTHD1VXjvLCEnZuQ3rJDE9L"),
    (Array[Byte](0x51, 0x6b, 0x6f, 0xcd, 0x0f), "ABnLTmg"),
    (Array[Byte](0xbf, 0x4f, 0x89, 0x00, 0x1e, 0x67, 0x02, 0x74, 0xdd), "3SEo3LWLoPntC"),
    (Array[Byte](0x57, 0x2e, 0x47, 0x94), "3EFU7m"),
    (Array[Byte](0xec, 0xac, 0x89, 0xca, 0xd9, 0x39, 0x23, 0xc0, 0x23, 0x21), "EJDM8drfXA6uyA"),
    (Array[Byte](0x10, 0xc8, 0x51, 0x1e), "Rt5zm"),
    (Array[Byte](0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00), "1111111111"),
    (Array(0, 1, 9, 102, 119, 96, 6, -107, 61, 85, 103, 67, -98, 94, 57, -8, 106, 13, 39, 59, -18, -42, 25, 103, -10), "16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM"),
    ("abcdefghijklmnopqrstuvwxyz", "3yxU3u1igY8WkgtjK92fbJQCd4BZiiT1v25f"),
    ("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", "QNoRV1sxwosKt47hNWXhBXyUVZxxn47YeXdAG7cLaAuKKaMzmF2NCzpV2Tz6mBSvvnHZ5PF"),
    ("""abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+-=><,./';":][}{\|""", "2ANFG5vFY32kSKHtuXNf5yqv3phrEExWbRXtZRMbrdtpKuR39uZvwgFaSz7RsJBqx1b36CsqtQLy8zcf5JBzXckGnNf7KBEJtn5akLMzmiFEKGck5t31n2LAkwk2w"),
    (new String((0 to 127).map{ _.toByte }.toArray, UTF_8), "12Mc55eJJSx97JNqXmW7TuJSoE9N3JhfHo9hbKS9Pm2qUNVUH1Tgd6xAdqeP8xAxDhjuR6vmAu7KR9Kt2PqaMEEcD2ThugBHeVLCJtW1V1iMZ13ZJfDH7h8E11Xz9hpEDcPUikMCe5p9EHLv7Y3wNXT2bBNYZarpY7Trn3cm8vvo7t"),
    (Hex.decodeHex("00010966776006953D5567439E5E39F86A0D273BEED61967F6".toArray), "16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM")
  )

  test("Basic Encoding and Decoding") {
    data.foreach{ case (bytes, encoded) => check(bytes, encoded) }
  }

  test("encodeChecked/decodeChecked - examples with known values") {
    // Note: This does not include the version
    val bytes: Array[Byte] = Hex.decodeHex("010966776006953D5567439E5E39F86A0D273BEE".toArray)
    val encoded: String = "16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM"

    Base58.encodeChecked(0, bytes) shouldBe encoded
    Base58.decodeChecked(encoded) shouldBe 0.toByte +: bytes
  }

  test("encodeChecked - examples with known values") {
    Base58.encodeChecked(111, new Array[Byte](20)) shouldBe "mfWxJ45yp2SFn7UciZyNpvDKrzbhyfKrY8"
    Base58.encodeChecked(128, new Array[Byte](32)) shouldBe "5HpHagT65TZzG1PH3CSu63k8DbpvD8s5ip4nEB3kEsreAbuatmU"
  }

  test("decodeChecked - examples that should decode") {
    Base58.decodeChecked("4stwEBjT6FYyVV")
    Base58.decodeChecked("93VYUMzRG9DdbRP72uQXjaWibbQwygnvaCu9DumcqDjGybD864T")
  }

  private def check(bytes: Array[Byte], encoded: String): Unit = {

    Base58.encode(bytes) shouldBe encoded
    Base58.encode(bytes, 0, bytes.length) shouldBe encoded
    Base58.encode("foo".getBytes(UTF_8)++bytes++"bar".getBytes(UTF_8), 3, bytes.length) shouldBe encoded

    // def decode(data: String)
    Base58.decode(encoded) shouldBe bytes
    Base58.decode(encoded.toCharArray) shouldBe bytes

    // def decode(data: Array[Char])
    Base58.decode(encoded.toCharArray) shouldBe bytes

    checkChecked(0, bytes)
    checkChecked(1, bytes)
    checkChecked(255, bytes)
  }

  private def checkChecked(version: Int, bytes: Array[Byte]): Unit = {
    val checked: String = Base58.encodeChecked(version, bytes)

    // Note: the decodeChecked result includes the version
    Base58.decodeChecked(checked) shouldBe version.toByte +: bytes

    // Also check without the version
    checkChecked(bytes)
  }

  private def checkChecked(bytes: Array[Byte]): Unit = {
    val checked: String = Base58.encodeChecked(bytes)

    // Note: the decodeChecked result includes the version
    Base58.decodeChecked(checked) shouldBe bytes
  }
}
