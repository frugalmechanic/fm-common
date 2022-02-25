/*
 * Copyright 2018 Frugal Mechanic (http://frugalmechanic.com)
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

import scala.scalajs.js

// Similar to js.UndefOr, except it also handles null's
@js.native
sealed trait UndefNullOr[+A] extends js.Any

object UndefNullOr {
  implicit def toUndefOrOps[A](self: UndefNullOr[A]): js.UndefOrOps[A] = {
    if (self.isNull || js.isUndefined(self)) js.undefined else self.asInstanceOf[js.UndefOr[A]]
  }
}