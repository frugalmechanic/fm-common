/*
 * Copyright 2016 Frugal Mechanic (http://frugalmechanic.com)
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
package fm

import scala.util.Try

package object common extends Implicits {
  /** No Native java.time.Instant support in Scala JS so create a dummy class */
  final case class Instant(epochMilli: Long)

  /** No Native java.time.LocalDate support in Scala JS so create a dummy class */
  final case class LocalDate(year: Int, month: Int, day: Int)

  /** A type alias for java.net.URL */
  type URL = java.net.URI
  
  /** A type alias for java.net.URI */
  type URI = java.net.URI
  
  /**
   * Simple wrappers for the java.net.URL constructors
   */
  object URL {
    /** 
     * Create a URL
     * 
     * @param url The url
     * @return The URL instance
     */
    def apply(url: String): URL = new java.net.URI(url)
    
    /**
     * Try to create a URL
     * 
     * @param url The url
     * @return A Try[URL]
     */
    def tryParse(url: String): Try[URL] = Try{ apply(url) }
    
    /**
     * Same as URL.tryParse(url).toOption
     */
    def get(url: String): Option[URL] = tryParse(url).toOption
  }
  
  /**
   * Simple wrappers for the java.net.URI constructors
   */
  object URI {
    def apply(uri: String): URI = new java.net.URI(uri)
    def tryParse(uri: String): Try[URI] = Try{ apply(uri) }
    def get(uri: String): Option[URI] = tryParse(uri).toOption
  }
}