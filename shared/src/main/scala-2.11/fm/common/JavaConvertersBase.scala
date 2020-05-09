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
  def asJava[K, V](m: concurrent.Map[K, V]): ConcurrentMap[K, V] = mapAsJavaConcurrentMapConverter(m).asJava
  def asJava[K, V](m: Map[K, V]): java.util.Map[K, V] = mapAsJavaMapConverter(m).asJava
  def asJava[K, V](m: mutable.Map[K, V]): java.util.Map[K, V] = mutableMapAsJavaMapConverter(m).asJava
  def asJava[A](s: Set[A]): java.util.Set[A] = setAsJavaSetConverter(s).asJava
  def asJava[A](s: mutable.Set[A]): java.util.Set[A] = mutableSetAsJavaSetConverter(s).asJava
  def asJava[A](s: Seq[A]): java.util.List[A] = seqAsJavaListConverter(s).asJava
  def asJava[A](s: mutable.Seq[A]): java.util.List[A] = mutableSeqAsJavaListConverter(s).asJava
  def asJava[A](b: mutable.Buffer[A]): java.util.List[A] = bufferAsJavaListConverter(b).asJava
  def asJava[A](i: Iterable[A]): java.lang.Iterable[A] = asJavaIterableConverter(i).asJava
  def asJava[A](i: Iterator[A]): java.util.Iterator[A] = asJavaIteratorConverter(i).asJava
  def asJavaCollection[A](i: Iterable[A]): Collection[A] = asJavaCollectionConverter(i).asJavaCollection
  def asJavaDictionary[K, V](m: mutable.Map[K, V]): Dictionary[K, V] = asJavaDictionaryConverter(m).asJavaDictionary
  def asJavaEnumeration[A](i: Iterator[A]): java.util.Enumeration[A] = asJavaEnumerationConverter(i).asJavaEnumeration


  // Implement the 2.13 AsScalaConverters new method names
  def asScala(p: Properties): mutable.Map[String, String] = propertiesAsScalaMapConverter(p).asScala
  def asScala[A, B](d: Dictionary[A, B]): mutable.Map[A, B] = dictionaryAsScalaMapConverter(d).asScala
  def asScala[A, B](m: ConcurrentMap[A, B]): concurrent.Map[A, B] = mapAsScalaConcurrentMapConverter(m).asScala
  def asScala[A, B](m: java.util.Map[A, B]): mutable.Map[A, B] = mapAsScalaMapConverter(m).asScala
  def asScala[A](s: java.util.Set[A]): mutable.Set[A] = asScalaSetConverter(s).asScala
  def asScala[A](l: java.util.List[A]): mutable.Buffer[A] = asScalaBufferConverter(l).asScala
  def asScala[A](c: Collection[A]): Iterable[A] = collectionAsScalaIterableConverter(c).asScala
  def asScala[A](i: java.lang.Iterable[A]): Iterable[A] = iterableAsScalaIterableConverter(i).asScala
  def asScala[A](e: java.util.Enumeration[A]): Iterator[A] = enumerationAsScalaIteratorConverter(e).asScala
  def asScala[A](i: java.util.Iterator[A]): Iterator[A] = asScalaIteratorConverter(i).asScala
}
