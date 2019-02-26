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

import org.scalatest.{FunSuite, Matchers}

final class TestQueryParams extends FunSuite with Matchers {
  test("Basic Parsing") {
    ident("foo=bar")
    ident("foo=bar&asd=qwe")
    ident("foo=bar&foo+bar=asd+qwe")
    ident("foo=one&bar=asd&foo=two")
    ident("first=this+is+a+field&second=was+it+clear+%28already%29%3F")
    ident("param=foo%3Dbar%26asd%3Dq%20w%20e")
  }
  
  test("getFirst") {
    QueryParams("foo=bar").getFirst("foo") shouldBe Some("bar")
    QueryParams("foo=bar&foo=123").getFirst("foo") shouldBe Some("bar")
    QueryParams("foo=&foo=123").getFirst("foo") shouldBe Some("")
    
    QueryParams("foo").getFirst("foo") shouldBe None
    QueryParams("foo=bar").getFirst("asd") shouldBe None
  }
  
  test("getFirstNonBlank") {
    QueryParams("foo=bar").getFirstNonBlank("foo") shouldBe Some("bar")
    QueryParams("foo=bar&foo=123").getFirstNonBlank("foo") shouldBe Some("bar")
    QueryParams("foo=&foo=123").getFirstNonBlank("foo") shouldBe Some("123")
    QueryParams("foo= &foo=123").getFirstNonBlank("foo") shouldBe Some("123")
    
    QueryParams("foo").getFirstNonBlank("foo") shouldBe None
    QueryParams("foo=").getFirstNonBlank("foo") shouldBe None
    QueryParams("foo=&foo= ").getFirstNonBlank("foo") shouldBe None
  }
  
  test("nonNull") {
    QueryParams("foo=bar").nonNull("foo") shouldBe Seq("bar")
    QueryParams("foo=bar&foo&foo=&foo=qwe").nonNull("foo") shouldBe Seq("bar", "", "qwe")
    
    intercept[NoSuchElementException] { QueryParams("foo").nonNull("foo") }    
  }
  
  test("nonBlank") {
    QueryParams("foo=bar").nonBlank("foo") shouldBe Seq("bar")
    QueryParams("foo=bar&foo&foo=&foo= &foo=qwe").nonBlank("foo") shouldBe Seq("bar", "qwe")
    
    intercept[NoSuchElementException] { QueryParams("foo").nonBlank("foo") }
    intercept[NoSuchElementException] { QueryParams("foo=").nonBlank("foo") }
    intercept[NoSuchElementException] { QueryParams("foo= ").nonBlank("foo") }
  }
  
  test("Updated - Basic") {
    QueryParams("foo=bar").updated("foo", "new").toString shouldBe "foo=new"
    QueryParams("foo=bar").updated("foo").toString shouldBe "foo"
  }
  
  test("Contains Key") {
    QueryParams("foo=bar").contains("foo") shouldBe true
    QueryParams("foo=bar").contains("bar") shouldBe false
    QueryParams("foo=one&bar=asd&foo=two").contains("foo") shouldBe true
    QueryParams("foo=one&bar=asd&foo=two").contains("bar") shouldBe true
    QueryParams("foo=one&bar=asd&foo=two").contains("asd") shouldBe false
  }
  
  test("Contains Key/Value") {
    QueryParams("foo=bar").contains("foo","bar") shouldBe true
    QueryParams("foo=bar").contains("foo","foo") shouldBe false
    QueryParams("foo=bar").contains("bar","foo") shouldBe false
    QueryParams("foo=one&bar=asd&foo=two").contains("foo","two") shouldBe true
    QueryParams("foo=one&bar=asd&foo=two").contains("bar","asd") shouldBe true
    QueryParams("foo=one&bar=asd&foo=two").contains("foo","") shouldBe false
    QueryParams("foo=one&bar=asd&foo=two").contains("foo","three") shouldBe false
    QueryParams("foo=one&bar=asd&foo=two").contains("asd","bar") shouldBe false
  }
  
  test("Replace") {
    QueryParams("foo=bar").replace("foo", "new").toString shouldBe "foo=new"
    QueryParams("foo=bar").replace("bar", "new").toString shouldBe "foo=bar"
    QueryParams("foo=one&bar=asd&foo=two").replace("foo", "new").toString shouldBe "foo=new&bar=asd"
    QueryParams("foo=one&bar=asd&foo=two").replace("bar", "new").toString shouldBe "foo=one&bar=new&foo=two"
    QueryParams("foo=one&bar=asd&foo=two").replace("asd", "new").toString shouldBe "foo=one&bar=asd&foo=two"
  }
  
  test("Escapes") {
    QueryParams("foo=bar").updated("foo", "foo & bar").toString shouldBe "foo=foo+%26+bar"
    QueryParams("foo=foo+%26+bar").first("foo") shouldBe "foo & bar"
  }
  
  test("Updated - Duplicate Keys - Should update first and remove dupes") {
    QueryParams("foo=bar&foo=two").updated("foo", "new").toString shouldBe "foo=new"
    QueryParams("foo=bar&foo=two").updated("foo").toString shouldBe "foo"
  }
  
  test("Updated - Multiple parameters Should retain ordering") {
    QueryParams("a=a&foo=bar&b=b").updated("foo", "new").toString shouldBe "a=a&foo=new&b=b"
    QueryParams("a=a&foo=bar&b=b").updated("foo").toString shouldBe "a=a&foo&b=b"
  }
  
  test("Updated - Duplicate Key & Mutiple Parameters should retain ordering and remove dupes") {
    QueryParams("a=a&foo=bar&b=b&foo=asd").updated("foo", "new").toString shouldBe "a=a&foo=new&b=b"
    QueryParams("a=a&foo=bar&b=b&foo=asd").updated("foo").toString shouldBe "a=a&foo&b=b"
  }
  
  test("Remove - Basic") {
    QueryParams("foo=bar").remove("foo").toString shouldBe ""
    QueryParams("foo=bar&asd=qwe").remove("foo").toString shouldBe "asd=qwe"
  }
  
  test("Remove - Dupes") {
    QueryParams("foo=bar&foo=two").remove("foo").toString shouldBe ""
    QueryParams("foo=bar&asd=qwe&foo=two").remove("foo").toString shouldBe "asd=qwe"
  }
  
  test("allKeys") {
    QueryParams("").allKeys shouldBe Set.empty
    QueryParams("foo=bar&foo=two&a=b&c").allKeys shouldBe Set("foo", "a", "c")
  }
  
  test("keysWithValues") {
    QueryParams("").keysWithValues shouldBe Set.empty
    QueryParams("foo=bar&foo=two&a=b&c").keysWithValues shouldBe Set("foo", "a")
    QueryParams("foo=bar&foo=two&a=b&c=").keysWithValues shouldBe Set("foo", "a", "c")
  }
  
  test("keysWithNonBlankValues") {
    QueryParams("").keysWithNonBlankValues shouldBe Set.empty
    QueryParams("foo=bar&foo=two&a=b&c").keysWithNonBlankValues shouldBe Set("foo", "a")
    QueryParams("foo=bar&foo=two&a=b&c=").keysWithNonBlankValues shouldBe Set("foo", "a")
    QueryParams("foo=bar&foo=two&a=b&c= &d=    ").keysWithNonBlankValues shouldBe Set("foo", "a")
  }
  
  test("keysWithoutValues") {
    QueryParams("").keysWithoutValues shouldBe Set.empty
    QueryParams("foo=bar&foo=two&a=b&c").keysWithoutValues shouldBe Set("c")
  }
  
  test("nulls") {
    val nullPair: (String,String) = null
    val nullKeyValue: (String,String) = (null,null)
    
    QueryParams(nullPair).toString shouldBe ""
    QueryParams(nullPair) shouldBe QueryParams.empty
    
    QueryParams(nullKeyValue).toString shouldBe ""
    QueryParams(nullKeyValue) shouldBe QueryParams.empty
    
    QueryParams.empty.updated(nullPair) shouldBe QueryParams.empty
    QueryParams.empty.updated(nullKeyValue) shouldBe QueryParams.empty
  }
  
  test("URL") {
    val url: URL = URL("http://www.gopjn.com/t/1-1539-47154-1539?url=http%3A%2F%2Fwww.trailerhitches.com%2Fcarriers%2Fcargo-carriers%2Fthulesportrackfrontierroofracka21003s.cfm%3FTID%3DTGP058")
    QueryParams(url).toString shouldBe "url=http%3A%2F%2Fwww.trailerhitches.com%2Fcarriers%2Fcargo-carriers%2Fthulesportrackfrontierroofracka21003s.cfm%3FTID%3DTGP058"
    url.query.get should equal("url=http%3A%2F%2Fwww.trailerhitches.com%2Fcarriers%2Fcargo-carriers%2Fthulesportrackfrontierroofracka21003s.cfm%3FTID%3DTGP058")
    url.updateQueryParam("url", "foo & bar").query.get should equal("url=foo+%26+bar")
    url.updateQueryParam("url", " Hello & World! ").toString should equal("http://www.gopjn.com/t/1-1539-47154-1539?url=+Hello+%26+World%21+")
  }
  
  test("URI") {
    val uri: URI = URI("http://www.gopjn.com/t/1-1539-47154-1539?url=http%3A%2F%2Fwww.trailerhitches.com%2Fcarriers%2Fcargo-carriers%2Fthulesportrackfrontierroofracka21003s.cfm%3FTID%3DTGP058")
    QueryParams(uri).toString shouldBe "url=http%3A%2F%2Fwww.trailerhitches.com%2Fcarriers%2Fcargo-carriers%2Fthulesportrackfrontierroofracka21003s.cfm%3FTID%3DTGP058"
    uri.query.get should equal("url=http%3A%2F%2Fwww.trailerhitches.com%2Fcarriers%2Fcargo-carriers%2Fthulesportrackfrontierroofracka21003s.cfm%3FTID%3DTGP058")
    uri.updateQueryParam("url", "foo & bar").query.get should equal("url=foo+%26+bar")
    uri.updateQueryParam("url", " Hello & World! ").toString should equal("http://www.gopjn.com/t/1-1539-47154-1539?url=+Hello+%26+World%21+")
  }
  
  private def ident(queryString: String) {
    // Note: QueryParams always encodes spaces as pluses ('+') so in order to match we need to
    //       replace '%20' with '+' in case the original queryString used %20 instead of pluses
    QueryParams(queryString).toString shouldBe queryString.replace("%20","+")
  }
  
//  private def check(queryString: String, expectedQueryString: String) {
//    QueryParams(queryString).toString shouldBe expectedQueryString
//  }
}