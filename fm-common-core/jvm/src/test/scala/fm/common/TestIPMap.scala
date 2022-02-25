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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestIPMap extends AnyFunSuite with Matchers {

  private def yes[T](map: IPMapMutable[T], ip: String, value: T): Unit = TestHelpers.withCallerInfo{ check(map, ip, Option(value)) }
  private def no[T](map: IPMapMutable[T], ip: String): Unit = TestHelpers.withCallerInfo{ check(map, ip, None) }

  private def check[T](map: IPMapMutable[T], ip: String, res: Option[T]): Unit = {
    checkImpl(map, ip, res)
    checkImpl(map.result(), ip, res)
  }
  
  private def checkImpl[T](map: IPMap[T], ip: String, res: Option[T]): Unit = {
    map.get(ip) shouldBe res
    map.contains(ip) shouldBe res.isDefined
  }
  
  implicit class RichIPMapMutable(val map: IPMapMutable[String]) {
    def += (ip: String): this.type = {
      map += ((IPOrSubnet(ip), ip))
      this
    }
  }

  test("Basic Operations") {
    val map = IPMapMutable[String]()
    
    map += "192.168.0.123"

    def check192(): Unit = {
      yes(map, "192.168.0.123", "192.168.0.123")

      no(map, "192.168.0.122")
      no(map, "192.168.0.124")
      no(map, "0.0.0.123")
    }

    def check127(): Unit = {
      yes(map, "127.0.0.0", "127.0.0.0/8")
      yes(map, "127.255.255.255", "127.0.0.0/8")
      yes(map, "127.123.123.123", "127.0.0.0/8")

      no(map, "126.0.0.0")
      no(map, "126.1.2.3")
      no(map, "126.255.255.255")
      no(map, "128.0.0.0")
      no(map, "128.1.2.3")
      no(map, "128.255.255.255")
    }

    check192()

    map += "127.0.0.0/8"

    yes(map, "127.0.0.123", "127.0.0.0/8")

    check127()
    check192()

    map += "127.0.0.123"

    yes(map, "127.0.0.123", "127.0.0.123")

    check127()
    check192()

    map += "0.0.0.0/16"

    yes(map, "0.0.0.0", "0.0.0.0/16")
    yes(map, "0.0.1.2", "0.0.0.0/16")
    yes(map, "0.0.123.123", "0.0.0.0/16")
    yes(map, "0.0.255.255", "0.0.0.0/16")

    no(map, "0.1.0.0")
    no(map, "0.123.0.0")
    no(map, "0.255.0.0")

    map += "0.0.0.0/0"

    yes(map, "0.0.0.0", "0.0.0.0/16")
    yes(map, "0.1.0.0", "0.0.0.0/0")
    yes(map, "0.123.0.0", "0.0.0.0/0")
    yes(map, "0.255.0.0", "0.0.0.0/0")
    yes(map, "0.0.0.1", "0.0.0.0/16")
    yes(map, "0.1.2.3", "0.0.0.0/0")
    yes(map, "1.2.3.4", "0.0.0.0/0")
    yes(map, "128.128.128.128", "0.0.0.0/0")
    yes(map, "255.0.0.0", "0.0.0.0/0")
    yes(map, "255.255.255.255", "0.0.0.0/0")

    map += "1.2.3.4"
    yes(map, "1.2.3.4", "1.2.3.4")

    map += "255.255.0.0/16"
    yes(map, "255.255.1.2", "255.255.0.0/16")
    yes(map, "255.123.4.5", "0.0.0.0/0")

    map += "255.0.0.0/8"
    yes(map, "255.255.1.2", "255.255.0.0/16")
    yes(map, "255.123.4.5", "255.0.0.0/8")
    yes(map, "255.123.123.123", "255.0.0.0/8")
  }
}
