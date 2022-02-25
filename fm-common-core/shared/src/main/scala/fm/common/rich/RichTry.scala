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
package fm.common.rich

import scala.util.{Failure, Success, Try}

final class RichTry[T](val self: Try[T]) extends AnyVal {
  /**
   * If this is a Failure then map the Exception to another possible exception
   */
  @inline def mapFailure(f: Throwable => Throwable): Try[T] = self match {
    case _: Success[T] => self
    case Failure(ex) => Failure(f(ex))
  }
}