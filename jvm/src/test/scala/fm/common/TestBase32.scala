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

final class TestBase32 extends FunSuite with Matchers {
  private[this] val data: Vector[(String,String)] = Vector(
    "" -> "",
    "Hello World" -> "jbswy3dpeblw64tmmq======",
    "abcdefghijklmnopqrstuvwxyz" -> "mfrggzdfmztwq2lknnwg23tpobyxe43uov3ho6dzpi======",
    "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz" -> "mfrggzdfmztwq2lknnwg23tpobyxe43uov3ho6dzpjqwey3emvtgo2djnjvwy3lon5yhc4ttor2xm53ypf5a====",
    """abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+-=><,./';":][}{\|""" -> "mfrggzdfmztwq2lknnwg23tpobyxe43uov3ho6dzpjaueq2eivdeoscjjjfuytkoj5ifcustkrkvmv2ylfndcmrtgq2tmnzyheyccqbdeqsv4jrkfauv6kznhu7dylbof4ttwir2lvnx2624pq======",
    new String((0 to 127).map{ _.toByte }.toArray, UTF_8) -> "aaaqeayeaudaocajbifqydiob4ibceqtcqkrmfyydenbwha5dypsaijcemsckjrhfausukzmfuxc6mbrgiztinjwg44dsor3hq6t4p2aifbegrcfizduqskkjnge2tspkbiveu2ukvlfowczljnvyxk6l5qgcytdmrswmz3infvgw3dnnzxxa4lson2hk5txpb4xu634pv7h6==="
  )
  
  test("Basic Encoding and Decoding") {
    data.foreach{ case (original, encoded) => check(original, encoded) }
  }
  
  private def check(original: String, encoded: String): Unit = {
    val bytes: Array[Byte] = original.getBytes(UTF_8)
    val encodedNoPadding: String = stripPadding(encoded)
    
    Base32.encode(bytes) shouldBe encoded
    Base32.encodeUpper(bytes) shouldBe encoded.toUpperCase
    
    Base32.encodeNoPadding(bytes) shouldBe encodedNoPadding
    Base32.encodeUpperNoPadding(bytes) shouldBe encodedNoPadding.toUpperCase
    
    // def decode(data: String)
    Base32.decode(encoded) shouldBe bytes
    Base32.decode(encoded.toCharArray) shouldBe bytes
    Base32.decode(encoded.toCharArray) shouldBe bytes
    
    Base32.decode(encodedNoPadding) shouldBe bytes
    Base32.decode(encodedNoPadding.toCharArray) shouldBe bytes

    // def decode(data: Array[Char])
    Base32.decode(encoded.toCharArray) shouldBe bytes
    Base32.decode(encoded.toLowerCase.toCharArray) shouldBe bytes
    Base32.decode(encoded.toUpperCase.toCharArray) shouldBe bytes

    Base32.decode(encodedNoPadding.toLowerCase.toCharArray) shouldBe bytes
    Base32.decode(encodedNoPadding.toUpperCase.toCharArray) shouldBe bytes
  }
  
  private def stripPadding(s: String): String = s.replaceAll("=", "")
}
