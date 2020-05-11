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

import java.util.Optional
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

final class RichOption[A](val self: Option[A]) extends AnyVal {
  /**
   * Essentially flatMap{ a: A => Try{ f(a) }.toOption }
   */
  def tryMap[B](f: A => B): Option[B] = self match {
    case Some(a) => Try{ f(a) }.toOption
    case None    => None
  }

  /**
   * Converts an Option[Future[B]] to a Future[Option[B]]
   */
  def transform[B](implicit ctx: ExecutionContext, ev: A <:< Future[B]): Future[Option[B]] = {
    self match {
      case Some(f) => f.map{ res: B => Option(res) }
      case None    => Future.successful(None)
    }
  }

  /**
   * Implements asJava method similar to collection.JavaConverters._ for scala Option class
   */
  def asJava: Optional[A] = self match {
    case Some(v) => Optional.ofNullable[A](v)
    case None    => Optional.empty[A]
  }
}