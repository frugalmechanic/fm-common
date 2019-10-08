/*
 * Copyright 2017 Frugal Mechanic (http://frugalmechanic.com)
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

import java.time.LocalDate

final class RichLocalDate(val date: LocalDate) extends AnyVal with Ordered[LocalDate] {
  def compare(that: LocalDate): Int = date compareTo that

  def toYYYYMMDDOption: Option[String] = if (isWithinYYYYRange) Some(toYYYYMMDD) else None

  /**
   * Returns the YYYYMMDD string representation of the date.  Works for the range: 0000-01-01 to 9999-12-31
   */
  def toYYYYMMDD: String = {
    yyyyRangeCheck()

    val year: Int = date.getYear
    val month: Int = date.getMonthValue
    val day: Int = date.getDayOfMonth

    val sb: java.lang.StringBuilder = new java.lang.StringBuilder(8)

    if (year <= 9) sb.append('0')
    if (year <= 99) sb.append('0')
    if (year <= 999) sb.append('0')
    sb.append(year)

    if (month <= 9) sb.append('0')
    sb.append(month)

    if (day <= 9) sb.append('0')
    sb.append(day)

    sb.toString()
  }

  def toYYYYMMDDIntOption: Option[Int] = if (isWithinYYYYRange) Some(toYYYYMMDDInt) else None

  /**
   * Returns the YYYYMMDD int representation of the date.  Works for the range: 0000-01-01 to 9999-12-31
   */
  def toYYYYMMDDInt: Int = {
    yyyyRangeCheck()
    date.getYear * 10000 + date.getMonthValue * 100 + date.getDayOfMonth
  }

  private def isWithinYYYYRange: Boolean = {
    val year: Int = date.getYear
    year >= 0 && year <= 9999
  }

  private def yyyyRangeCheck(): Unit = {
    if (!isWithinYYYYRange) throw new IllegalArgumentException("toYYYYMMDD is only supported for years <= 9999 and >= 0")
  }
}
