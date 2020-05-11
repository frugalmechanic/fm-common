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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TestIP extends AnyFunSuite with Matchers {

  test("get") {
    IP.get("") should equal(None)
  }
  
  test("New IP") {
    val str = "255.255.255.255"

    IP.toInt(str) should equal(-1)
    IP.toLong(str) should equal(IP.MAX_IP)
  }

  testIpConversions("216.9.0.141", 3624468621L, Array(216,9,0,141))

  testIpConversions("65.249.66.235", 1106854635L, Array(65,249,66,235))
  testIpConversions("66.249.66.235", 1123631851L, Array(66,249,66,235))

  testIpConversions("0.0.0.0", 0L, Array(0,0,0,0))
  testIpConversions("127.127.127.127", 2139062143L, Array(127,127,127,127))
  testIpConversions("126.127.128.129", 2122285185L, Array(126,127,128,129))
  testIpConversions("1.2.3.4", 16909060L, Array(1,2,3,4))
  testIpConversions("255.255.255.255", 4294967295L, Array(255,255,255,255))

  test("Equals") {
    val ip1 = IP("65.249.66.235")
    val ip2 = IP("66.249.66.235")

    ip1 shouldBe ip1
    ip2 shouldBe ip2

    ip1 should not equal (ip2)
    ip2 should not equal (ip1)
  }

  test("isValid") {
    def check(ip: String, valid: Boolean): Unit = TestHelpers.withCallerInfo{ IP.isValid(ip) should equal(valid) }

    check("", false)
    check("1", false)  // Might work in the apply method but isValid only checks for xxx.xxx.xxx.xxx notation
    
    check("1.2", false)
    check("1.2.3", false)
    check("123.123.123", false)

    check("0.0.0.0", true)
    check("1.1.1.1", true)
    check("123.123.123.123", true)
    check("255.255.255.255", true)
    
    check("a.b.c.d", false)

    check("256.256.256.256", false)
    check("256256.256256.256256.256256", false)
    check("256.1.2.3", false)
    check("0.0.0.256", false)
    check("-1.1.1.1", false)
    check("1.1.1.-1", false)
  }
  
  test("findAllIPsIn") {
    def check(str: String, matches: Seq[String]): Unit = TestHelpers.withCallerInfo{ IP.findAllIPsIn(str) should equal(matches.map{IP.apply}.toIndexedSeq) }
    
    check("", Nil)
    check("1.2.3", Nil)
    check("1.2.3.4", Seq("1.2.3.4"))
    check("1.2.3.4.", Seq("1.2.3.4"))
    check("a1.2.3.4.", Nil)
    check("1.2.3.4. 5.6.7.8", Seq("1.2.3.4", "5.6.7.8"))
    check("1.2.3.4.5.6.7.8", Nil)
    check("1.2.3.4-5.6.7.8", Nil)
    check("1.2.3.4_5.6.7.8", Nil)
    check(" 1.2.3.4.  5.6.7.8 ", Seq("1.2.3.4", "5.6.7.8"))
    check(" 1.2.3.4. foo  5.6.7.8 bar \t 127.0.0.1", Seq("1.2.3.4", "5.6.7.8", "127.0.0.1"))
    check(" 1.2.3.4.,5.6.7.8 ,127.0.0.1, 1.2.3.4", Seq("1.2.3.4", "5.6.7.8", "127.0.0.1", "1.2.3.4"))
  }

  def testIpConversions(ipStr: String, ipLong: Long, ipOctets: Array[Int]): Unit = {
    test("IP Conversions - "+ipStr) {
      IP.toLong(IP.toInt(ipLong)) should equal(ipLong)

      IP.toLong(ipStr) shouldBe ipLong
      IP.toInt(ipLong) shouldBe IP.toInt(ipStr)
      IP.toString(ipLong) should equal(ipStr)
      IP.toIntArray(ipLong).toList.toString should equal(ipOctets.toList.toString)
      IP.toLong(ipOctets).toString shouldBe ipLong.toString

      val ip = IP(ipStr)
      val ipFromLong = IP(ipLong.toString)

      ip shouldBe IP(ipLong)
      ip shouldBe IP(ipStr)
      ip.longValue shouldBe ipLong
      ip.toString shouldBe ipStr
      ipFromLong should equal(ip)
    }
  }
}
