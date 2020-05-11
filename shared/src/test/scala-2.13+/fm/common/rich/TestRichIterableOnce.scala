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
package fm.common.rich

import fm.common.Implicits._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TestRichIterableOnce extends AnyFunSuite with Matchers with ScalaFutures {
  
  test("minOption") {
    Vector.empty[Int].minOption shouldBe None
    Vector(1,2,3).minOption shouldBe Some(1)
    Vector(3,2,1).minOption shouldBe Some(1)
  }
  
  test("maxOption") {
    Vector.empty[Int].maxOption shouldBe None
    Vector(1,2,3).maxOption shouldBe Some(3)
    Vector(3,2,1).maxOption shouldBe Some(3)
  }
  
  test("mkStringOrBlank") {
    Vector().mkStringOrBlank("[start]", "[sep]", "[end]") shouldBe ""
    Vector("foo").mkStringOrBlank("[start]", "[sep]", "[end]") shouldBe "[start]foo[end]"
    Vector("foo", "bar").mkStringOrBlank("[start]", "[sep]", "[end]") shouldBe "[start]foo[sep]bar[end]"
  }
  
  test("countBy") {
    Vector(1,2,2,3,3,3,4,4,4,4).countBy{ i => i} shouldBe Map(1 -> 1, 2 -> 2, 3 -> 3, 4 -> 4)
  }
  
  test("collapseBy") {
     Vector(2,4,6,1,3,2,5,7).collapseBy{ _ % 2 == 0 } shouldBe Vector((true,Vector(2, 4, 6)), (false,Vector(1, 3)), (true,Vector(2)), (false,Vector(5, 7)))
  }
  
  test("findMapped") {
    Vector(1,2,3,4).findMapped{ i: Int => if (i % 2 == 0) Some("foo") else None } shouldBe Some("foo")
    Vector(1,2,3,4).findMapped{ i: Int => if (i == 123) Some("foo") else None } shouldBe None
  }

  // Failing in Scala.JS due to the usage of Futures:
//  test("findMappedFuture") {
//    Vector(1,2,3,4).findMappedFuture{ i: Int => if (i == 2) Future.successful(Some("foo")) else Future.successful(None) }.futureValue shouldBe Some("foo")
//    Vector(1,2,3).findMappedFuture{ i: Int => if (i == 123) Future.successful(Some("foo")) else Future.successful(None) }.futureValue shouldBe None
//    
//    // Only the Future for the first element should be run.
//    Vector(1,2,3,4).findMappedFuture{ i: Int => if (i == 1) Future.successful(Some("foo")) else { System.exit(-1); ??? } }.futureValue shouldBe Some("foo")
//  }
}
