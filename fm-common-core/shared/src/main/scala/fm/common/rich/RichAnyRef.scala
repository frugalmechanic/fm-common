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
package fm.common.rich

import scala.reflect.ClassTag

final class RichAnyRef[A <: AnyRef](val a: A) extends AnyVal {
  def tryCast[B <: A](implicit classTag: ClassTag[B]): Option[B] = if (classTag.runtimeClass.isInstance(a)) Some(a.asInstanceOf[B]) else None
  def tryCast[B <: A](cls: Class[B]): Option[B] = if (cls.isInstance(a)) Some(a.asInstanceOf[B]) else None
}
