/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 by Lloyd Chan
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * This is from: https://github.com/lloydmeta/enumeratum
 */
package fm.common

/**
 * Created by Lloyd on 4/11/16.
 *
 * Copyright 2016
 */
sealed trait ValueEnumEntry[ValueType] {

  /**
   * Value of this entry
   */
  def value: ValueType

}

object ValueEnumEntry {

  implicit class ValueEnumOps[A <: ValueEnumEntry[_]](val enumEntry: A) extends AnyVal {

    /**
     * Checks if the current enum value is contained by the set of enum values in the parameter list.
     *
     * @param firstEnum First enum of the list.
     * @param otherEnums Remaining enums.
     * @return `true` if the current value is contained by the parameter list.
     */
    def in(firstEnum: A, otherEnums: A*): Boolean = in(firstEnum +: otherEnums)

    /**
     * Checks if the current enum value is contained by the set of enum values in the parameter list.
     *
     * @param entries First enum of the list.
     * @return `true` if the current value is contained by the parameter list.
     */
    def in(entries: Seq[A]): Boolean = entries.contains(enumEntry)

  }
}

/**
 * Value Enum Entry parent class for [[Int]] valued entries
 */
abstract class IntEnumEntry extends ValueEnumEntry[Int]

/**
 * Value Enum Entry parent class for [[Long]] valued entries
 */
abstract class LongEnumEntry extends ValueEnumEntry[Long]

/**
 * Value Enum Entry parent class for [[Short]] valued entries
 */
abstract class ShortEnumEntry extends ValueEnumEntry[Short]

/**
 * Value Enum Entry parent class for [[String]] valued entries
 *
 * This is similar to [[enumeratum.Enum]], but different in that values must be
 * literal values. This restraint allows us to enforce uniqueness at compile time.
 *
 * Note that uniqueness is only guaranteed if you do not do any runtime string manipulation on values.
 */
abstract class StringEnumEntry extends ValueEnumEntry[String]

/**
 * Value Enum Entry parent class for [[Byte]] valued entries
 */
abstract class ByteEnumEntry extends ValueEnumEntry[Byte]

/**
 * Value Enum Entry parent class for [[Char]] valued entries
 */
abstract class CharEnumEntry extends ValueEnumEntry[Char]