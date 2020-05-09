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

import scala.collection._

/**
 * This is the same as scala.collection.JavaConverters with a few additional methods.
 * 
 * Specifically you can now call .asScalaNullToEmpty to have null java collections
 * converted to an empty collection to avoid NullPointerExceptions
 */
object JavaConverters extends JavaConvertersBase {

  implicit class RichJavaList[A](val l: java.util.List[A]) extends AnyVal {
    def asScalaNullToEmpty(): mutable.Buffer[A] = {
      if (l == null) mutable.ArrayBuffer.empty
      else asScala(l)
    }
  }

  implicit class RichJavaIterator[A](val i: java.util.Iterator[A]) extends AnyVal {
    def asScalaNullToEmpty(): Iterator[A] = {
      if (i == null) Iterator.empty
      else asScala(i)
    }
  }

  implicit class RichJavaSet[A, B](val s: java.util.Set[A]) extends AnyVal {
    def asScalaNullToEmpty(): mutable.Set[A] = {
      if (s == null) mutable.Set.empty
      else asScala(s)
    }
  }

  implicit class RichJavaCollection[A, B](val c: java.util.Collection[A]) extends AnyVal {
    def asScalaNullToEmpty(): Iterable[A] = {
      if (c == null) Iterable.empty
      else asScala(c)
    }
  }

  implicit class RichJavaDictionary[A, B](val m: java.util.Dictionary[A, B]) extends AnyVal {
    def asScalaNullToEmpty(): mutable.Map[A, B] = {
      if (m == null) mutable.Map.empty
      else asScala(m)
    }
  }

  implicit class RichJavaEnumeration[A, B](val e: java.util.Enumeration[A]) extends AnyVal {
    def asScalaNullToEmpty(): Iterator[A] = {
      if (e == null) Iterator.empty
      else asScala(e)
    }
  }

  implicit class RichJavaIterable[A, B](val it: java.lang.Iterable[A]) extends AnyVal {
    def asScalaNullToEmpty(): Iterable[A] = {
      if (it == null) Iterable.empty
      else asScala(it)
    }
  }

  implicit class RichJavaConcurrentMap[A, B](val m: java.util.concurrent.ConcurrentMap[A, B]) extends AnyVal {
    def asScalaNullToEmpty(): collection.concurrent.Map[A, B] = {
      if (m == null) collection.concurrent.TrieMap.empty
      else asScala(m)
    }
  }

  implicit class RichJavaMap[A, B](val m: java.util.Map[A, B]) extends AnyVal {
    def asScalaNullToEmpty(): mutable.Map[A, B] = {
      if (m == null) mutable.Map.empty
      else asScala(m)
    }
  }

  implicit class RichJavaProperties(val p: java.util.Properties) extends AnyVal {
    def asScalaNullToEmpty(): mutable.Map[String, String] = {
      if (p == null) mutable.Map.empty
      else asScala(p)
    }
  }
}