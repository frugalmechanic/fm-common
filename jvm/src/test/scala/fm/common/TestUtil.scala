/*
 * Copyright 2019 Frugal Mechanic (http://frugalmechanic.com)
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
import scala.concurrent.TimeoutException
import scala.concurrent.duration._

final class TestUtil extends FunSuite with Matchers {
  private def timeout(timeout: FiniteDuration, sleep: FiniteDuration): Boolean = {
    Util.timeout(timeout) {
      Thread.sleep(sleep.toMillis)
      true
    }
  }

  test("timeout") {
    timeout(2.seconds, 1.second) shouldBe true

    assertThrows[TimeoutException] {
      timeout(1.second, 5.seconds) shouldBe false
    }
  }
}