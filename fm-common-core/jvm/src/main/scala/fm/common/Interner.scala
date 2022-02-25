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

import com.google.common.collect.{Interner => GoogleInterner, Interners => GoogleInterners}

/**
 * Wraper around Guava's Interner class.
 * 
 * Originally I had a generic Intern object that had a map of Class[_] to Guava Interner
 * but that relies on a correct implementation of equals which can make it error
 * prone.  For Example an Option[Char] can == an Option[Int].  So instead you have
 * to be explicit about creating an Interner.
 */
final case class Interner[T <: AnyRef]() {
  private[this] val interner: GoogleInterner[T] = GoogleInterners.newWeakInterner()

  def apply(value: T): T = interner.intern(value)
}
