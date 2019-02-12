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

import java.nio.charset.StandardCharsets.UTF_8
import org.scalatest.{FunSuite, Matchers}

final class TestBase32Hex extends FunSuite with Matchers {
  private[this] val data: Vector[(String,String)] = Vector(
    "" -> "",
    "Hello World" -> "91imor3f41bmusjccg======",
    "abcdefghijklmnopqrstuvwxyz" -> "c5h66p35cpjmgqbaddm6qrjfe1on4srkelr7eu3pf8======",
    "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" -> "c5h66p35cpjmgqbaddm6qrjfe1on4srkelr7eu3pf9gm4or4clj6eq39d9lmorbedto72sjjehqnctrof5t0====",
    """abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+-=><,./';":][}{\|""" -> "c5h66p35cpjmgqbaddm6qrjfe1on4srkelr7eu3pf90k4gq48l34ei29995kojae9t852kijahalclqob5d32chj6gqjcdpo74o22g134gils9ha50kluapd7kv3ob1e5sjjm8hqbldnquqsfg======",
    new String((0 to 127).map{ _.toByte }.toArray, UTF_8) -> "000g40o40k30e209185go38e1s8124gj2gahc5oo34d1m70t3ofi08924ci2a9h750kikapc5kn2uc1h68pj8d9m6ss3iehr7gujsfq085146h258p3kgiaa9d64qjifa18l4kqkalb5em2pb9dlonaubtg62oj3chimcpr8d5l6mr3ddpnn0sbiedq7atjnf1snkursflv7u==="
  )
  
  test("Basic Encoding and Decoding") {
    data.foreach{ case (original, encoded) => check(original, encoded) }
  }
  
  private def check(original: String, encoded: String): Unit = {
    val bytes: Array[Byte] = original.getBytes(UTF_8)
    val encodedNoPadding: String = stripPadding(encoded)
    
    Base32Hex.encode(bytes) shouldBe encoded
    Base32Hex.encodeUpper(bytes) shouldBe encoded.toUpperCase
    
    Base32Hex.encodeNoPadding(bytes) shouldBe encodedNoPadding
    Base32Hex.encodeUpperNoPadding(bytes) shouldBe encodedNoPadding.toUpperCase
    
    // def decode(data: String)
    Base32Hex.decode(encoded) shouldBe bytes
    Base32Hex.decode(encoded.toCharArray) shouldBe bytes
    Base32Hex.decode(encoded.toCharArray) shouldBe bytes
    
    Base32Hex.decode(encodedNoPadding) shouldBe bytes
    Base32Hex.decode(encodedNoPadding.toCharArray) shouldBe bytes
    Base32Hex.decode(encodedNoPadding.toCharArray) shouldBe bytes
    
    // def decode(data: Array[Char])
    Base32Hex.decode(encoded.toCharArray) shouldBe bytes
    Base32Hex.decode(encoded.toLowerCase.toCharArray) shouldBe bytes
    Base32Hex.decode(encoded.toUpperCase.toCharArray) shouldBe bytes
    
    Base32Hex.decode(encodedNoPadding.toCharArray) shouldBe bytes
    Base32Hex.decode(encodedNoPadding.toLowerCase.toCharArray) shouldBe bytes
    Base32Hex.decode(encodedNoPadding.toUpperCase.toCharArray) shouldBe bytes
  }
  
  private def stripPadding(s: String): String = s.replaceAll("=", "")
}
