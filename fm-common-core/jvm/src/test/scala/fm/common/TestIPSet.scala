/*
 * Copyright 2015 Frugal Mechanic (http://frugalmechanic.com)
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

final class TestIPSet extends AnyFunSuite with Matchers {

  private def yes(set: IPSetMutable, ip: String): Unit = TestHelpers.withCallerInfo{ check(set, ip, true) }
  private def no(set: IPSetMutable, ip: String): Unit = TestHelpers.withCallerInfo{ check(set, ip, false) }
  
  private def check(set: IPSetMutable, ip: String, res: Boolean): Unit = {
    set.contains(ip) shouldBe res
    set.result().contains(ip) shouldBe res
  }
  
  test("Contains") {
    val set = IPSetMutable()
    set += "192.168.0.123"
    
    def check192(): Unit = {
      yes(set, "192.168.0.123")
      
      no(set, "192.168.0.122")
      no(set, "192.168.0.124")
      no(set, "0.0.0.123")
    }
    
    def check127(): Unit = {
      yes(set, "127.0.0.0")
      yes(set, "127.255.255.255")
      yes(set, "127.123.123.123")
      yes(set, "127.0.0.123")
      
      no(set, "126.0.0.0")
      no(set, "126.1.2.3")
      no(set, "126.255.255.255")
      no(set, "128.0.0.0")
      no(set, "128.1.2.3")
      no(set, "128.255.255.255")
    }
    
    check192()
    
    set += "127.0.0.0/8"

    check127()
    check192()
    
    set += "127.0.0.123"
    
    check127()
    check192()
    
    set += "0.0.0.0/16"
    
    yes(set, "0.0.0.0")
    yes(set, "0.0.1.2")
    yes(set, "0.0.123.123")
    yes(set, "0.0.255.255")
    
    no(set, "0.1.0.0")
    no(set, "0.123.0.0")
    no(set, "0.255.0.0")
    
    set.hasDefaultRoute shouldBe false
    set.hasQuadZero shouldBe false
    
    set += "0.0.0.0/0"
    
    set.hasDefaultRoute shouldBe true
    set.hasQuadZero shouldBe true
    
    yes(set, "0.0.0.0")
    yes(set, "0.1.0.0")
    yes(set, "0.123.0.0")
    yes(set, "0.255.0.0")
    yes(set, "0.0.0.1")
    yes(set, "0.1.2.3")
    yes(set, "1.2.3.4")
    yes(set, "128.128.128.128")
    yes(set, "255.0.0.0")
    yes(set, "255.255.255.255")
  }
}
