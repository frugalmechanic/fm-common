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
package fm.common

import org.scalatest.{FunSuite, Matchers}

final class TestIPSubnet extends FunSuite with Matchers {
  import IPSubnet._
  
  test("Check Private") {
    def check(ip: String, isPrivate: Boolean) = TestHelpers.withCallerInfo{ IP(ip).isPrivate should equal(isPrivate) }
    
    Seq(
      "192.168.0.1",
      "192.168.123.123",
      "192.168.255.255",
      "127.0.0.1",
      "10.0.0.0",
      "10.255.255.255",
      "10.10.0.1",
      "10.10.255.255"
    ).foreach{ check(_, isPrivate = true) }
    
    Seq(
      "123.123.123.123",
      "216.9.0.141",
      "192.167.255.255",
      "192.169.0.1",
      "9.255.255.255",
      "11.0.0.0"
    ).foreach{ check(_, isPrivate = false) }
    
  }
  
  test("isValidMask") {
    isValidMask(IP("255.255.255.255")) shouldBe true
    isValidMask(IP("255.255.255.0")) shouldBe true
    isValidMask(IP("255.255.0.0")) shouldBe true
    isValidMask(IP("255.0.0.0")) shouldBe true
    isValidMask(IP("0.0.0.0")) shouldBe true
    
    isValidMask(IP("0.0.0.255")) shouldBe false
    isValidMask(IP("0.0.255.255")) shouldBe false
    isValidMask(IP("0.255.255.255")) shouldBe false
    
    isValidMask(IP("0.0.0.2")) shouldBe false
    isValidMask(IP("0.0.0.4")) shouldBe false
    isValidMask(IP("0.4.0.0")) shouldBe false
  }
  
  test("isValidRange") {
    isValidRange(IP("192.168.0.0"), IP("192.168.0.255")) shouldBe true
    isValidRange(IP("192.168.0.0"), IP("192.168.255.255")) shouldBe true
    
    isValidRange(IP("192.168.0.255"), IP("192.168.0.0")) shouldBe false
    isValidRange(IP("192.168.0.0"), IP("192.168.255.0")) shouldBe false
    
    isValidRange(IP("192.168.0.0"), IP("192.168.255.0")) shouldBe false
    isValidRange(IP("192.168.0.0"), IP("192.168.255.254")) shouldBe false
    isValidRange(IP("192.168.0.0"), IP("192.168.254.255")) shouldBe false
  }

  test("isValidCIDR") {
    IPSubnet.isValidCIDR(IP("0.0.0.0"), 0) shouldBe true

    IPSubnet.isValidCIDR(IP("0.0.0.0"), 8) shouldBe true
    IPSubnet.isValidCIDR(IP("0.0.0.0"), 16) shouldBe true
    IPSubnet.isValidCIDR(IP("0.0.0.0"), 24) shouldBe true
    IPSubnet.isValidCIDR(IP("0.0.0.0"), 32) shouldBe true

    IPSubnet.isValidCIDR(IP("128.0.0.0"), 1) shouldBe true
    IPSubnet.isValidCIDR(IP("128.0.0.0"), 2) shouldBe true
    IPSubnet.isValidCIDR(IP("128.0.0.0"), 32) shouldBe true

    IPSubnet.isValidCIDR(IP("255.255.255.255"), 0) shouldBe false
    IPSubnet.isValidCIDR(IP("255.255.255.255"), 24) shouldBe false
    IPSubnet.isValidCIDR(IP("255.255.255.255"), 32) shouldBe true
  }

  test("Invalid Subnets") {
    an [IllegalArgumentException] should be thrownBy IPSubnet("34.215.149.214/0")
  }
  
  test("parse - 192.168.0.0/24") {
    def check(subnet: IPSubnet): Unit = TestHelpers.withCallerInfo{ subnet.toString should equal("192.168.0.0/24") }
    
    check(parse("192.168.0.0/24"))
    check(parse("192.168.0.0 - 192.168.0.255"))
    check(parse("192.168.0.0-192.168.0.255"))
    check(forRangeOrMask(IP("192.168.0.0"), IP("255.255.255.0")))
    check(forRangeOrMask(IP("192.168.0.0"), IP("192.168.0.255")))
    check(forMask(IP("192.168.0.0"), IP("255.255.255.0")))
    check(forRange(IP("192.168.0.0"), IP("192.168.0.255")))
    
    val net = IPSubnet.parse("192.168.0.0/24")
    
    net.isQuadZero shouldBe false
    net.isDefaultRoute shouldBe false

    net.start shouldBe IP("192.168.0.0")
    net.end shouldBe IP("192.168.0.255")

    net.contains(IP("192.168.0.0")) shouldBe true
    net.contains(IP("192.168.0.1")) shouldBe true
    net.contains(IP("192.168.0.254")) shouldBe true
    net.contains(IP("192.168.0.255")) shouldBe true
    net.contains(IP("192.168.0.128")) shouldBe true
    
    net.contains(IP("192.168.1.0")) shouldBe false
    net.contains(IP("192.168.255.0")) shouldBe false
    net.contains(IP("191.168.0.0")) shouldBe false
    net.contains(IP("193.168.0.0")) shouldBe false
  }

  test("127.0.0.0/8") {
    val net = IPSubnet.parse("127.0.0.0/8")

    net.isQuadZero shouldBe false
    net.isDefaultRoute shouldBe false

    net.start shouldBe IP("127.0.0.0")
    net.end shouldBe IP("127.255.255.255")

    net.contains(IP("127.0.0.0")) shouldBe true
    net.contains(IP("127.1.2.3")) shouldBe true
    net.contains(IP("127.255.255.255")) shouldBe true

    net.contains(IP("0.0.0.0")) shouldBe false
    net.contains(IP("1.2.3.4")) shouldBe false
    net.contains(IP("128.128.128.128")) shouldBe false
    net.contains(IP("255.255.255.255")) shouldBe false
  }

  test("0.0.0.0/0") {
    val net = IPSubnet.parse("0.0.0.0/0")
    
    net.isQuadZero shouldBe true
    net.isDefaultRoute shouldBe true

    net.start shouldBe IP("0.0.0.0")
    net.end shouldBe IP("255.255.255.255")

    net.contains(IP("0.0.0.0")) shouldBe true
    net.contains(IP("1.2.3.4")) shouldBe true
    net.contains(IP("128.128.128.128")) shouldBe true
    net.contains(IP("255.255.255.255")) shouldBe true
  }
}
