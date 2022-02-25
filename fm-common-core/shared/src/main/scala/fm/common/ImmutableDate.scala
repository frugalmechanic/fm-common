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

import java.util.Date
import scala.concurrent.duration.FiniteDuration
import scala.math.{Ordered, Ordering}

object ImmutableDate {
  /** lossless conversion so I think this is okay as an implicit */
  implicit def immutableDateToJavaDate(date: ImmutableDate): Date = {
    if (null == date) null else date.toDate
  }

  /** lossless conversion so I think this is okay as an implicit */
  implicit def javaDateToImmutableDate(date: Date): ImmutableDate = {
    if (null == date) null else ImmutableDate(date)
  }

  /** lossless conversion so I think this is okay as an implicit */
  implicit def immutableDateToJavaInstant(date: ImmutableDate): Instant = {
    if (null == date) null else date.toInstant
  }

  /** This is a lossless conversion from Date to ImmutableDate */
  def apply(date: Date): ImmutableDate = {
    if (null == date) null else new ImmutableDate(date.getTime)
  }

  /** This is a potentially lossy conversion from java.time.Instant to ImmutableDate */
  def apply(instant: Instant): ImmutableDate = {
    if (null == instant) null else new ImmutableDate(InstantCompat.toEpochMilli(instant))
  }

  implicit object ordering extends Ordering[ImmutableDate] {
    def compare(a: ImmutableDate, b: ImmutableDate): Int = a.compare(b)
  }

  def apply(): ImmutableDate = new ImmutableDate()

  def now(): ImmutableDate = apply()
}

/**
 * Represents an immutable java.util.Date
 *
 * This provides an immutable milliseconds since epoch representation of a date
 * when it might be a mismatch to use the newer java.time.Instance (which represents things
 * as nanoseconds since or before epoch)
 *
 * NOTE: NOT extending AnyVal so that nulls still work (just like with java.util.Date)
 */
final case class ImmutableDate(millis: Long) extends Ordered[ImmutableDate] {
  def this() = this(System.currentTimeMillis())

  def toDate: Date = new Date(millis)
  def toInstant: Instant = InstantCompat.ofEpochMilli(millis)

  def getTime: Long = millis

  def compare(that: ImmutableDate): Int = {
    if (millis < that.millis) -1
    else if (millis == that.millis) 0
    else 1
  }

  def +(other: ImmutableDate): ImmutableDate = ImmutableDate(millis + other.millis)
  def -(other: ImmutableDate): ImmutableDate = ImmutableDate(millis - other.millis)

  def +(other: FiniteDuration): ImmutableDate = ImmutableDate(millis + other.toMillis)
  def -(other: FiniteDuration): ImmutableDate = ImmutableDate(millis - other.toMillis)

  override def toString: String = toDate.toString()
}
