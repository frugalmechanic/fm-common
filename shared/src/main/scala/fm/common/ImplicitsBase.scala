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

import fm.common.rich._
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}
import java.time.{Instant, LocalDate}
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}
import scala.math.{BigDecimal => ScalaBigDecimal, BigInt => ScalaBigInt}

/**
 * These are the Implicits that are shared between both the JVM and JS Implicits trait/object
 */
protected trait ImplicitsBase extends OrderingImplicits {
  implicit def toRichAnyRef[A <: AnyRef](ref: A): RichAnyRef[A] = new RichAnyRef[A](ref)
  implicit def toAnyRefNullChecks[A <: AnyRef](ref: A): AnyRefNullChecks[A] = new AnyRefNullChecks[A](ref)

  implicit def toRichChar(ch: Char): RichChar = new RichChar(ch)

  implicit def toRichCharSequence(s: CharSequence): RichCharSequence = new RichCharSequence(s)
  implicit def toRichString(s: String): RichString = new RichString(s)
  
  implicit def toRichTraversableOnce[T](t: scala.collection.TraversableOnce[T]) = new RichTraversableOnce(t)
  implicit def toRichIndexedSeq[T](t: scala.collection.IndexedSeq[T]) = new RichIndexedSeq(t)
  
  implicit def toRichStringOption(opt: Option[String]): RichStringOption = new RichStringOption(opt)
  implicit def toRichIntOption(opt: Option[Int]): RichIntOption = new RichIntOption(opt)
  implicit def toRichLongOption(opt: Option[Long]): RichLongOption = new RichLongOption(opt)
  implicit def toRichBooleanOption(opt: Option[Boolean]): RichBooleanOption = new RichBooleanOption(opt)
  implicit def toRichCharOption(opt: Option[Char]): RichCharOption = new RichCharOption(opt)

  implicit def toRichOption[T](opt: Option[T]): RichOption[T] = new RichOption[T](opt)
  
  implicit def toTypeSafeEquals[L](left: L): TypeSafeEquals[L] = new TypeSafeEquals(left)
  
  implicit def bigIntegerOrdering: Ordering[JavaBigInteger] = RichBigInteger
  implicit def toRichBigInteger(i: JavaBigInteger): RichBigInteger = new RichBigInteger(i)
  implicit def toRichBigInteger(i: ScalaBigInt): RichBigInteger = new RichBigInteger(i.bigInteger)
  
  implicit def bigDecimalOrdering: Ordering[JavaBigDecimal] = RichBigDecimal
  implicit def toRichBigDecimal(d: JavaBigDecimal): RichBigDecimal = new RichBigDecimal(d)
  implicit def toRichBigDecimal(d: ScalaBigDecimal): RichBigDecimal = new RichBigDecimal(d.bigDecimal)
  
  implicit def toRichAtomicInteger(int: AtomicInteger): RichAtomicInteger =  new RichAtomicInteger(int)
  implicit def toRichAtomicLong(long: AtomicLong): RichAtomicLong =  new RichAtomicLong(long)
  
  implicit def toRichInstant(instant: Instant): RichInstant = new RichInstant(instant)
  implicit def toRichLocalDate(date: LocalDate): RichLocalDate = new RichLocalDate(date)
  
  implicit def toRichMap[A,B,This <: scala.collection.MapLike[A,B,This] with scala.collection.Map[A,B]](m: scala.collection.MapLike[A,B,This]) = new RichMap(m)
  implicit def toRichConcurrentMap[K,V](m: java.util.concurrent.ConcurrentMap[K,V]): RichConcurrentMap[K,V] = new RichConcurrentMap(m)
  
  implicit def toRichPattern(pattern: java.util.regex.Pattern): RichPattern = new RichPattern(pattern)
  implicit def toRichPattern(regex: scala.util.matching.Regex): RichRegex = new RichRegex(regex)
  
  implicit def toRichURI(uri: URI): RichURI = new RichURI(uri)
}