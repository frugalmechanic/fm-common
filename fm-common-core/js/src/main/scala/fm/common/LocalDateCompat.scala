/*
 * Copyright 2022 Tim Underwood (https://github.com/tpunder)
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

private object LocalDateCompat extends LocalDateCompatBase {
  override def of(year: Int, month: Int, day: Int): LocalDate = LocalDate(year, month, day)

  override def compare(a: LocalDate, b: LocalDate): Int = {
    val yearRes: Int = a.year compareTo b.year
    if (yearRes =!= 0) return yearRes

    val monthRes: Int = a.month compareTo b.month
    if (monthRes =!= 0) return monthRes

    a.day compare b.day
  }

  override def getYear(date: LocalDate): Int = date.year
  override def getMonthValue(date: LocalDate): Int = date.month
  override def getDayOfMonth(date: LocalDate): Int = date.day
}
