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

import fm.common.Implicits.toRichQueryParamsObject
import fm.common.{Interner, QueryParams, URI, URL}

object RichURL {
  private val interner: Interner[URL] = Interner()
}

final class RichURL(val self: URL) extends AnyVal with RichURIBase[URL] {
  def scheme: Option[String] = Option(self.getProtocol)
  def userInfo: Option[String] = Option(self.getUserInfo)
  def host: Option[String] = Option(self.getHost)
  def port: Option[Int] = if (-1 == self.getPort) None else Some(self.getPort)
  def path: Option[String] = Option(self.getPath)
  def query: Option[String] = Option(self.getQuery)
  def fragment: Option[String] = Option(self.getRef)
  def queryParams: QueryParams = QueryParams(self)
  protected def make(s: String): URL = new URL(s)
  
  protected def toURI: URI = self.toURI()
//  protected def toURL: URL = self

  def intern: URL = RichURL.interner(self)
}