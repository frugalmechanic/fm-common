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

object BitUtils {
  /**
   * Create a long out of 2 ints such that the first int is the upper 32 bits of the long
   * and the second int is the lower 32 bits of the long.
   * 
   * [a - upper 32 bits][b - lower 32 bits]
   */
  def makeLong(a: Int, b: Int): Long = ((a: Long) << 32) | (b & 0xffffffffL)

  def makeInt(a: Short, b: Short): Int = ((a: Int) << 16) | (b & 0xffff)
  
  /**
   * Split a long into 2 ints (the reverse of makeLong())
   */
  def splitLong(long: Long): (Int, Int) = (getUpper(long), getLower(long))

  /**
   * Split an int into 2 shorts (the reverse of makeInt())
   */
  def splitInt(int: Int): (Short, Short) = (getUpper(int), getLower(int))
  
  /**
   * Get the upper 32 bits of the long
   */
  def getUpper(long: Long): Int = (long >> 32).toInt

  /**
   * Get the upper 16 bits of the int
   */
  def getUpper(int: Int): Short = (int >> 16).toShort

  /**
   * Get the lower 32 bits of the long
   */
  def getLower(long: Long): Int = long.toInt

  /**
   * Get the lower 16 bits of the int
   */
  def getLower(int: Int): Short = int.toShort

  /**
   * Get the upper 24 bits of the int
   */
  def getUpper24Bits(int: Int): Int = (int >> 8) & 0xffffff

  /**
   * Get the upper 8 bits of the int
   */
  def getUpper8Bits(int: Int): Short = ((int >> 24) & 0xff).toShort

  /**
   * Get the lower 24 bits of the int
   */
  def getLower24Bits(int: Int): Int = int & 0xffffff

  /**
   * Get the lower 8 bits of the int
   */
  def getLower8Bits(int: Int): Short = (int & 0xff).toShort

  /**
   * Makes an int that combines an upper 24 bits (from an int) with the lower 8 bits (from a short)
   * @param a The upper 24 bits
   * @param b The lower 8 bits
   * @return The combined int
   */
  def makeIntWithUpper24Bits(a: Int, b: Short): Int = {
    if (!isWithin24Bits(a) || !isWithin8Bits(b)) throw new IllegalArgumentException("Arguments out of range")
    (a << 8) | ((b: Int) & 0xff)
  }

  /**
   * Makes an int that combines an upper 8 bits (from a short) with the lower 24 bits (from an int)
   * @param a The upper 8 bits
   * @param b The lower 24 bits
   * @return The combined int
   */
  def makeIntWithLower24Bits(a: Short, b: Int): Int = {
    if (!isWithin8Bits(a) || !isWithin24Bits(b)) throw new IllegalArgumentException("Arguments out of range")
    ((a: Int) << 24) | (b & 0xffffff)
  }

  private def isWithin24Bits(a: Int): Boolean = (a & 0xffffff) == a
  private def isWithin8Bits(a: Short): Boolean = (a & 0xff) == a
}