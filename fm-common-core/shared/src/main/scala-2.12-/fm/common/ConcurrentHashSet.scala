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
package fm.common

import java.lang.{Boolean => JavaBoolean}
import java.util.concurrent.{ConcurrentHashMap => JavaConcurrentHashMap}
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * EXPERIMENTAL - A Scala mutable Set based on ConcurrentHashMap
 */
final class ConcurrentHashSet[A](map: JavaConcurrentHashMap[A, JavaBoolean]) extends mutable.Set[A] {
  def this(initialCapacity: Int, loadFactor: Float, concurrencyLevel: Int) = this(new JavaConcurrentHashMap[A, JavaBoolean](initialCapacity, loadFactor, concurrencyLevel))
  def this(initialCapacity: Int, loadFactor: Float) = this(initialCapacity, loadFactor, 16)
  def this(initialCapacity: Int) = this(initialCapacity, 0.75f)
  def this() = this(16)
  
  def asJava: JavaConcurrentHashSet[A] = new JavaConcurrentHashSet(map)
  
  def contains(key: A): Boolean = map.containsKey(key)
  
  def iterator: Iterator[A] = map.keySet().iterator().asScala
  
  def +=(elem: A): this.type = { map.put(elem, JavaBoolean.TRUE); this }
  
  def -=(elem: A): this.type = { map.remove(elem); this }
  
  override def empty: ConcurrentHashSet[A] = new ConcurrentHashSet()
  
  override def foreach[U](f: A => U): Unit = {
    val it = map.keySet.iterator()
    while(it.hasNext) f(it.next)
  }
  
  override def size: Int = map.size()
 
  override def hashCode: Int = map.hashCode
}
