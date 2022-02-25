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

import fm.common.{QueryParams, URI}
import fm.common.Implicits._
import java.io.File

trait RichURIBase[T] extends Any {
  def scheme: Option[String]
  def userInfo: Option[String]
  def host: Option[String]
  def port: Option[Int]
  def path: Option[String]
  def query: Option[String]
  def fragment: Option[String]
  def queryParams: QueryParams
  
  protected def self: T
  protected def make(s: String): T
  
  protected def toURI: URI
//  protected def toURL: URL

  /**
   * Is this a file:// URI/URL?
   */
  def isFile: Boolean = scheme.exists{ _ == "file" }
  
  def toFile: File = {
    require(isFile, "Not a file: "+toURI)
    new File(toURI)
  }
    
  def toFileOption: Option[File] = if (isFile) Some(new File(toURI)) else None

  def updateQueryParam(key: String, value: String): T = copyQueryParams(queryParams.updated(key, value))
  
  def updateQueryParams(kvPairs: (String, String)*): T = {
    if (kvPairs.isEmpty) self else copyQueryParams(queryParams.updated(kvPairs:_*))
  }
  
  def addQueryParam(key: String, value: String): T = copyQueryParams(queryParams.add(key, value))
  def addQueryParams(kvPairs: (String, String)*): T = copyQueryParams(queryParams.add(kvPairs: _*))
  def addQueryParams(other: QueryParams): T = copyQueryParams(queryParams.add(other))
  
  def removeQueryParam(key: String): T = copyQueryParams(queryParams.remove(key))
  def removeQueryParams(keys: String*): T = copyQueryParams(queryParams.remove(keys:_*))
  
  /**
   * Calls QueryParams.updated
   * 
   * If the key doesn't exist then add it, otherwise replace the first occurance
   * of the key with the new value and remove any other values.
   */
  def withQueryParams(params: (String, String)*): T = copyQueryParams(queryParams.updated(params:_*))
  
  /**
   * Calls QueryParams.updated
   * 
   * If the key doesn't exist then add it, otherwise replace the first occurance
   * of the key with the new value and remove any other values.
   */
  def withQueryParams(params: QueryParams): T = copyQueryParams(queryParams.updated(params))
  
  /**
   * Calls QueryParams.updated
   * 
   * If the key doesn't exist then add it, otherwise replace the first occurance
   * of the key with the new value and remove any other values.
   */
  def withQueryParam(key: String, value: String): T = withQueryParams(key -> value)
  
  /**
   * Calls QueryParams.updated
   * 
   * If the key doesn't exist then add it, otherwise replace the first occurance
   * of the key with the new value and remove any other values.
   */
  def withQueryParam(kv: (String, String)): T = withQueryParams(kv)
  def withoutQueryParams: T = copy(query = None)
  
  private def copyQueryParams(params: QueryParams): T = copy(query = params.toString.toBlankOption)

  def withScheme(scheme: String): T = copy(scheme = Option(scheme))
  def withHost(host: String): T = copy(host = Option(host))
  def withPath(path: String): T = copy(path = Option(path))

  // Strip schema/host/port/etc for creating HTML-friendly absolute URI paths agnostic of protocol/host/etc
  final def pathQueryAndFragmentURI: URI = toURI.copy(scheme = None, host = None, userInfo = None, port = None)
  final def pathAndQueryURI: URI = toURI.copy(scheme = None, host = None, userInfo = None, port = None, fragment = None)
  
  def copy(
    scheme: Option[String] = scheme,
    userInfo: Option[String] = userInfo,
    host: Option[String] = host,
    port: Option[Int] = port,
    path: Option[String] = path,
    query: Option[String] = query,
    fragment: Option[String] = fragment
  ): T = {
    // Can't use this since it messes with the encoding and you end up with a double or not encoded query string 
    //new URI(scheme.orNull, userInfo.orNull, host.orNull, port.getOrElse(-1), path.orNull, query.orNull, fragment.orNull)
    
    // From: URI Java Docs for the multi-arg constructor.  Building up the string the same way but not performing the same escaping they are doing.
    // 1. Initially, the result string is empty. 
    val sb = new StringBuilder
    
    // 2. If a scheme is given then it is appended to the result, followed by a colon character (':').
    sb ++= scheme.map{ _+":" }.getOrElse("")

    // 3. If user information, a host, or a port are given then the string "//" is appended. 
    if (userInfo.isDefined || host.isDefined || port.isDefined) sb ++= "//"
      
    // 4. If user information is given then it is appended, followed by a commercial-at character ('@'). Any character not in the unreserved, punct, escaped, or other categories is quoted.
    sb ++= userInfo.map{ _+"@" }.getOrElse("")
    
    // 5. If a host is given then it is appended. If the host is a literal IPv6 address but is not enclosed in square brackets ('[' and ']') then the square brackets are added. 
    sb ++= host.getOrElse("")
   
    // 6. If a port number is given then a colon character (':') is appended, followed by the port number in decimal. 
    sb ++= port.map{ ":"+_ }.getOrElse("")
   
    // 7. If a path is given then it is appended. Any character not in the unreserved, punct, escaped, or other categories, and not equal to the slash character ('/') or the commercial-at character ('@'), is quoted. 
    sb ++= path.getOrElse("")
   
    // 8. If a query is given then a question-mark character ('?') is appended, followed by the query. Any character that is not a legal URI character is quoted. 
    sb ++= query.map{ "?"+_ }.getOrElse("")
    
    // Should this filter out blank query params?
    // sb ++= query.filter{ _.isNotNullOrBlank }.map{ "?"+_ }.getOrElse("")
    
    // 9. Finally, if a fragment is given then a hash character ('#') is appended, followed by the fragment. Any character that is not a legal URI character is quoted.
    sb ++= fragment.map{ "#"+_ }.getOrElse("")
    
    make(sb.toString)
  }
}