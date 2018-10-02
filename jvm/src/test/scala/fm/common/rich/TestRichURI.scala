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

import org.scalatest.{FunSuite, Matchers}
import fm.common._

/**
 * Also see TestRichURL
 */
final class TestRichURI extends FunSuite with Matchers {
  test("Basics") {
    check("http://frugalmechanic.com", scheme = Some("http"), host = Some("frugalmechanic.com"), path = Some(""))
    
    check("https://user:pass@frugalmechanic.com:123/path?foo=bar#hash", 
      scheme = Some("https"),
      userInfo = Some("user:pass"),
      host = Some("frugalmechanic.com"),
      port = Some(123),
      path = Some("/path"),
      query = Some("foo=bar"),
      fragment = Some("hash"),
      queryParamsToCheck = Seq("foo" -> "bar")
    )
    
    check("https://user:pass@frugalmechanic.com:123/p%20a%3Dt%20h?param=foo%3Dbar%26asd%3Dq%20w%20e#hash", 
      scheme = Some("https"),
      userInfo = Some("user:pass"),
      host = Some("frugalmechanic.com"),
      port = Some(123),
      path = Some("/p%20a%3Dt%20h"), // This isn't decoded...
      //path = Some("/p a=t h"),
      query = Some("param=foo%3Dbar%26asd%3Dq%20w%20e"),
      fragment = Some("hash"),
      queryParamsToCheck = Seq("param" -> "foo=bar&asd=q w e")
    )
  }
  
  def check[T](s: String, scheme: Option[String] = None, userInfo: Option[String] = None, host: Option[String] = None, port: Option[Int] = None, path: Option[String] = None, query: Option[String] = None, fragment: Option[String] = None, queryParamsToCheck: Seq[(String,String)] = Nil): Unit = {
    def checkFields[X](uri: RichURIBase[X]): Unit = {
      uri.scheme should equal (scheme)
      uri.userInfo should equal (userInfo)
      uri.host should equal (host)
      uri.port should equal (port)
      uri.path should equal (path)
      uri.query should equal (query)
      uri.fragment should equal (fragment)
      
      queryParamsToCheck.foreach { case (k,v) =>
        uri.queryParams.hasKey(k) should equal (true)
        uri.queryParams.hasKeyWithValue(k) should equal (v.isNotNullOrBlank)
        uri.queryParams.apply(k).head should equal (v)
      }
    }
    
    def checkCopy[X](uri: RichURIBase[X]): Unit = {
      uri.port should equal (Some(999))
      uri.path should equal (Some("/new-path"))
    }

    val richURI: RichURI = new RichURI(URI(s))
    checkFields(richURI)
    
    val richURL: RichURL = new RichURL(URL(s))
    checkFields(richURL)
    
    checkCopy(new RichURI(richURI.copy(path = Some("/new-path"), port = Some(999))))
    checkCopy(new RichURL(richURL.copy(path = Some("/new-path"), port = Some(999))))
    
    richURI.withQueryParams("var" -> "new & param").query should equal(Some(richURI.queryParams.toString.toBlankOption.map{ _+"&" }.getOrElse("")+"var=new+%26+param"))
    richURL.withQueryParams("var" -> "new & param").query should equal(Some(richURL.queryParams.toString.toBlankOption.map{ _+"&" }.getOrElse("")+"var=new+%26+param"))
    
  }
}