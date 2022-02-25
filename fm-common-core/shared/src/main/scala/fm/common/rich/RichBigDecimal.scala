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
package fm.common.rich

import java.math.BigDecimal

object RichBigDecimal extends Ordering[BigDecimal] {
  def compare(a: BigDecimal, b: BigDecimal): Int = a.compareTo(b)
}

final class RichBigDecimal(val self: BigDecimal) extends AnyVal with Ordered[BigDecimal] {
  def isZero: Boolean = 0 == self.compareTo(BigDecimal.ZERO) // 0.00 != 0 so we have to use compareTo and check if the result is zero
  def isNotZero: Boolean = !isZero
  def isPositive: Boolean = this > BigDecimal.ZERO
  def isPositiveOrZero: Boolean = this >= BigDecimal.ZERO
  def isNegative: Boolean = this < BigDecimal.ZERO
  def isNegativeOrZero: Boolean = this <= BigDecimal.ZERO
  
  def isOne: Boolean = 0 == self.compareTo(BigDecimal.ONE) // 1.00 != 1 so we have to use compareTo and check if the result is zero
  def isNotOne: Boolean = !isOne
  
  def compare(that: BigDecimal): Int = self.compareTo(that)
  
  def +(other: BigDecimal): BigDecimal = self.add(other)
}