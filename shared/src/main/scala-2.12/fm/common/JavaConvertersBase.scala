/*
 * Copyright 2015 Frugal Mechanic (http://frugalmechanic.com)
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

import scala.collection.{concurrent, mutable}
import java.util.{Collection, Dictionary, Properties}
import java.util.concurrent.ConcurrentMap
import scala.collection.convert.{DecorateAsJava, DecorateAsScala}

private[common] trait JavaConvertersBase extends DecorateAsJava with DecorateAsScala {

  // Implement the 2.13 AsJavaConverters new method names
  def asJava[K, V](m: concurrent.Map[K, V]): ConcurrentMap[K, V] = mapAsJavaConcurrentMap(m)
  def asJava[K, V](m: Map[K, V]): java.util.Map[K, V] = mapAsJavaMap(m)
  def asJava[K, V](m: mutable.Map[K, V]): java.util.Map[K, V] = mutableMapAsJavaMap(m)
  def asJava[A](s: Set[A]): java.util.Set[A] = setAsJavaSet(s)
  def asJava[A](s: mutable.Set[A]): java.util.Set[A] = mutableSetAsJavaSet(s)
  def asJava[A](s: Seq[A]): java.util.List[A] = seqAsJavaList(s)
  def asJava[A](s: mutable.Seq[A]): java.util.List[A] = mutableSeqAsJavaList(s)
  def asJava[A](b: mutable.Buffer[A]): java.util.List[A] = bufferAsJavaList(b)
  def asJava[A](i: Iterable[A]): java.lang.Iterable[A] = asJavaIterable(i)
  def asJava[A](i: Iterator[A]): java.util.Iterator[A] = asJavaIterator(i)

  // Implement the 2.13 AsScalaConverters new method names
  def asScala(p: Properties): mutable.Map[String, String] = propertiesAsScalaMap(p)
  def asScala[A, B](d: Dictionary[A, B]): mutable.Map[A, B] = dictionaryAsScalaMap(d)
  def asScala[A, B](m: ConcurrentMap[A, B]): concurrent.Map[A, B] = mapAsScalaConcurrentMap(m)
  def asScala[A, B](m: java.util.Map[A, B]): mutable.Map[A, B] = mapAsScalaMap(m)
  def asScala[A](s: java.util.Set[A]): mutable.Set[A] = asScalaSet(s)
  def asScala[A](l: java.util.List[A]): mutable.Buffer[A] = asScalaBuffer(l)
  def asScala[A](c: Collection[A]): Iterable[A] = collectionAsScalaIterable(c)
  def asScala[A](i: java.lang.Iterable[A]): Iterable[A] = iterableAsScalaIterable(i)
  def asScala[A](e: java.util.Enumeration[A]): Iterator[A] = enumerationAsScalaIterator(e)
  def asScala[A](i: java.util.Iterator[A]): Iterator[A] = asScalaIterator(i)
}
