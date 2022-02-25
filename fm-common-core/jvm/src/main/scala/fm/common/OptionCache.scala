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

/**
 * Provides cached instances of Boolean/Char/Byte/Int/Long wrapped in Scala's Option type to avoid boxing and
 * allocations for commonly used values.  Similar to how the JDK Integer cache works.
 *
 * Caching behavior per type:
 *
 *   Boolean - true/false cached
 *   Byte - All bytes cached (0 to 255)
 *   Char - All ASCII chars cached
 *   Int - Defaults to -XX:AutoBoxCacheMax range, can override with fm.common.IntegerCache.low/fm.common.IntegerCache.high properties
 *   Long - Defaults to -XX:AutoBoxCacheMax range, can override with fm.common.IntegerCache.low/fm.common.IntegerCache.high properties
 */
object OptionCache extends OptionCacheBase {

  // Cache all ASCII Chars
  private val charCache: Array[Some[Char]] = {
    (0 to ASCIIUtil.MaxASCIICodePoint).map{ (i: Int) => Some(i.toChar) }.toArray
  }

  // Cache all Byte values
  private val byteCache: Array[Some[Byte]] = {
    (0 to 255).map{ (i: Int) => Some(i.toByte) }.toArray
  }

  private val shortCache: Array[Some[Short]] = {
    (IntegerCacheUtil.low to IntegerCacheUtil.high).filter{ _.isValidShort }.map{ (i: Int) => Some(i.toShort) }.toArray
  }

  private val intCache: Array[Some[Int]] = {
    (IntegerCacheUtil.low to IntegerCacheUtil.high).map{ (i: Int) => Some(i) }.toArray
  }

  private val longCache: Array[Some[Long]] = {
    (IntegerCacheUtil.low to IntegerCacheUtil.high).map{ (i: Int) => Some(i.toLong) }.toArray
  }

  private def intOrLongIdxOf(i: Int): Int = {
    idxOf(IntegerCacheUtil.low, IntegerCacheUtil.high, i)
  }

  private def shortIdxOf(i: Int): Int = {
    val low: Int = math.max(IntegerCacheUtil.low, Short.MinValue)
    val high: Int = math.min(IntegerCacheUtil.high, Short.MaxValue)

    idxOf(low, high, i)
  }

  private def idxOf(low: Int, high: Int, i: Int): Int = if (i >= low && i <= high) i - low else -1

  override def valueOf(v: Char): Some[Char] = if (v.toInt < charCache.length) charCache(v.toInt) else Some(v)

  override def valueOf(v: Byte): Some[Byte] = byteCache(java.lang.Byte.toUnsignedInt(v))

  override def valueOf(v: Short): Some[Short] = {
    val idx: Int = shortIdxOf(v)
    if (-1 === idx) Some(v)
    else shortCache(idx)
  }

  override def valueOf(v: Int): Some[Int] = {
    val idx: Int = intOrLongIdxOf(v)
    if (-1 === idx) Some(v)
    else intCache(idx)
  }

  override def valueOf(v: Long): Some[Long] = {
    if (v < Int.MinValue || v > Int.MaxValue) return Some(v)

    val idx: Int = intOrLongIdxOf(v.toInt)
    if (-1 === idx) Some(v)
    else longCache(idx)
  }
}
