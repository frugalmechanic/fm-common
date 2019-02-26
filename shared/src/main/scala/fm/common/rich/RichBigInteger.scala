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

import fm.common.Implicits.toRichSomeObject
import java.math.BigInteger

object RichBigInteger extends Ordering[BigInteger] {
  def compare(a: BigInteger, b: BigInteger): Int = a.compareTo(b)
  
  private val IntMax: BigInteger = BigInteger.valueOf(Int.MaxValue)
  private val IntMin: BigInteger = BigInteger.valueOf(Int.MinValue)
  
  private val LongMax: BigInteger = BigInteger.valueOf(Long.MaxValue)
  private val LongMin: BigInteger = BigInteger.valueOf(Long.MinValue)
}

final class RichBigInteger(val self: BigInteger) extends AnyVal with Ordered[BigInteger] {
  import RichBigInteger._
  
  def isZero: Boolean = self == BigInteger.ZERO
  def isNotZero: Boolean = !isZero
  def isPositive: Boolean = this > BigInteger.ZERO
  def isPositiveOrZero: Boolean = this >= BigInteger.ZERO
  def isNegative: Boolean = this < BigInteger.ZERO
  def isNegativeOrZero: Boolean = this <= BigInteger.ZERO
  
  def compare(that: BigInteger): Int = self.compareTo(that)
  
  def intValueExact(): Int = {
    if (lt(self, IntMin) || gt(self, IntMax)) throw new ArithmeticException("BigInteger is outside the range of an Int: "+self)
    self.intValue()
  }
  
  def intValueExactOption(): Option[Int] = try {
    Some.cached(intValueExact())
  } catch {
    case _: ArithmeticException => None
  }
  
  def longValueExact(): Long = {
    if (lt(self, LongMin) || gt(self, LongMax)) throw new ArithmeticException("BigInteger is outside the range of an Long: "+self)
    self.longValue()
  }
  
  def longValueExactOption(): Option[Long] = try {
    Some.cached(longValueExact())
  } catch {
    case _: ArithmeticException => None
  }
}