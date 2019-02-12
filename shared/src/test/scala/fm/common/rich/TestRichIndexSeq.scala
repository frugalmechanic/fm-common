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

import org.scalatest.{FunSuite,Matchers}
import fm.common.Implicits._

class TestRichIndexSeq extends FunSuite with Matchers {
  
  test("takeWhile") {
    Vector.empty[Int].countWhile{ _ == 0 } shouldBe 0
    Vector(1,1,1,2,2,3,3,3,3,3).countWhile{ _ == 0 } shouldBe 0
    Vector(1,1,1,2,2,3,3,3,3,3).countWhile{ _ == 1 } shouldBe 3
    Vector(1,1,1,2,2,3,3,3,3,3).countWhile{ _ == 2 } shouldBe 0
  }
  
  test("takeWhile with starting idx") {
    Vector.empty[Int].countWhile(0){ _ == 0 } shouldBe 0
    Vector.empty[Int].countWhile(5){ _ == 0 } shouldBe 0
    Vector(1,1,1,2,2,3,3,3,3,3).countWhile(0){ _ == 0 } shouldBe 0
    Vector(1,1,1,2,2,3,3,3,3,3).countWhile(0){ _ == 1 } shouldBe 3
    Vector(1,1,1,2,2,3,3,3,3,3).countWhile(0){ _ == 2 } shouldBe 0
    
    Vector(1,1,1,2,2,3,3,3,3,3).countWhile(100){ _ == 0 } shouldBe 0
    Vector(1,1,1,2,2,3,3,3,3,3).countWhile(3){ _ == 1 } shouldBe 0
    Vector(1,1,1,2,2,3,3,3,3,3).countWhile(4){ _ == 2 } shouldBe 1
  }
}
