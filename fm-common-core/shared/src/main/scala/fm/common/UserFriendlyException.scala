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

object UserFriendlyException {
  def apply(friendlyTitle: String, friendlyMessage: String): UserFriendlyException = apply(friendlyTitle, friendlyMessage, null)
  
  def apply(friendlyTitle: String, friendlyMessage: String, cause: Throwable): UserFriendlyException = SimpleUserFriendlyException(friendlyTitle, friendlyMessage, cause)
  
  /** Extract a UserFriendlyException from a Throwable (if possible) */
  def unapply(throwable: Throwable): Option[UserFriendlyException] = {
    if (null == throwable) None
    else throwable match {
      case ex: UserFriendlyException => Some(ex)
      case _ => unapply(throwable.getCause)
    }
  }

  private case class SimpleUserFriendlyException(friendlyTitle: String, friendlyMessage: String, cause: Throwable) extends UserFriendlyException(s"$friendlyTitle - $friendlyMessage", cause)
}

abstract class UserFriendlyException (message: String, cause: Throwable) extends Exception(message, cause) {
  def this(message: String) = this(message, null)
  def this(cause: Throwable) = this(Option(cause).map{ _.toString }.orNull, cause)
  
  def friendlyTitle: String
  def friendlyMessage: String
}