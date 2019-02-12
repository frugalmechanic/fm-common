/*
 * Copyright 2017 Frugal Mechanic (http://frugalmechanic.com)
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

import java.util.Locale
import org.scalatest.{FunSuite, Matchers}

final class TestRichLocale extends FunSuite with Matchers {
  import fm.common.Implicits._

  test("isValid / hasNonBlankValidLanguage / hasNonBlankValidCountry - Built-In Locales") {
    // Language+Country Built-In Locales
    checkLocale(Locale.CANADA)
    checkLocale(Locale.CANADA_FRENCH)
    checkLocale(Locale.CHINA)
    checkLocale(Locale.FRANCE)
    checkLocale(Locale.GERMANY)
    checkLocale(Locale.ITALY)
    checkLocale(Locale.JAPAN)
    checkLocale(Locale.KOREA)
    checkLocale(Locale.PRC)
    checkLocale(Locale.PRC)
    checkLocale(Locale.SIMPLIFIED_CHINESE)  // CN is the country: createConstant("zh", "CN");
    checkLocale(Locale.TRADITIONAL_CHINESE) // TW is the country: createConstant("zh", "TW");
    checkLocale(Locale.TAIWAN)
    checkLocale(Locale.US)
    checkLocale(Locale.UK)

    // Language Only Built-In Locales
    checkLocale(Locale.CHINESE, hasNonBlankValidCountry = false)
    checkLocale(Locale.ENGLISH, hasNonBlankValidCountry = false)
    checkLocale(Locale.FRENCH, hasNonBlankValidCountry = false)
    checkLocale(Locale.GERMAN, hasNonBlankValidCountry = false)
    checkLocale(Locale.ITALIAN, hasNonBlankValidCountry = false)
    checkLocale(Locale.JAPANESE, hasNonBlankValidCountry = false)
    checkLocale(Locale.KOREAN, hasNonBlankValidCountry = false)

    checkLocale(Locale.ROOT, isValid = true, hasNonBlankValidLanguage = false, hasNonBlankValidCountry = false)
  }

  test("isValid / hasNonBlankValidLanguage / hasNonBlankValidCountry - Manually Constructed Locales") {
    checkLocale(new Locale("en","US"))
    checkLocale(new Locale("en","US", "foo"))

    checkLocale(new Locale("",""), isValid = true, hasNonBlankValidLanguage = false, hasNonBlankValidCountry = false)
    checkLocale(new Locale("","",""), isValid = true, hasNonBlankValidLanguage = false, hasNonBlankValidCountry = false)
  }

  test("isValid / hasNonBlankValidLanguage / hasNonBlankValidCountry - Custom Tags") {
    checkTag("en-US") // Should be the same as Locale.US

    checkTag("en", hasNonBlankValidCountry = false)
    checkTag("de", hasNonBlankValidCountry = false)
    checkTag("fr", hasNonBlankValidCountry = false)

    checkTag("en-ZZ", isValid = false, hasNonBlankValidCountry = false)
    checkTag("de-ZZ", isValid = false, hasNonBlankValidCountry = false)
    checkTag("fr-ZZ", isValid = false, hasNonBlankValidCountry = false)

    checkTag("zz-ZZ", isValid = false, hasNonBlankValidLanguage = false, hasNonBlankValidCountry = false)
  }

  test("isChinese") {
    checkLanguageHelper(Locale.CHINESE, Locale.CHINA, Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE){ _.isChinese }
  }

  test("isEnglish") {
    checkLanguageHelper(Locale.ENGLISH, Locale.US, Locale.UK, Locale.CANADA){ _.isEnglish }
  }

  test("isFrench") {
    checkLanguageHelper(Locale.FRENCH, Locale.FRANCE, Locale.CANADA_FRENCH){ _.isFrench }
  }

  test("isGerman") {
    checkLanguageHelper(Locale.GERMAN, Locale.GERMANY){ _.isGerman }
  }

  test("isItalian") {
    checkLanguageHelper(Locale.ITALIAN, Locale.ITALY){ _.isItalian }
  }

  test("isJapanese") {
    checkLanguageHelper(Locale.JAPANESE, Locale.JAPAN){ _.isJapanese }
  }

  test("isKorean") {
    checkLanguageHelper(Locale.KOREAN, Locale.KOREA){ _.isKorean }
  }

  private def checkTag(tag: String, isValid: Boolean = true, hasNonBlankValidLanguage: Boolean = true, hasNonBlankValidCountry: Boolean = true): Unit = {
    checkLocale(Locale.forLanguageTag(tag), isValid = isValid, hasNonBlankValidLanguage = hasNonBlankValidLanguage, hasNonBlankValidCountry = hasNonBlankValidCountry)
  }

  private def checkLocale(locale: Locale, isValid: Boolean = true, hasNonBlankValidLanguage: Boolean = true, hasNonBlankValidCountry: Boolean = true): Unit = {
    withClue(s"Locale: $locale   (Expected == Actual | isValid: $isValid == ${locale.isValid} | hasNonBlankValidLanguage: $hasNonBlankValidLanguage == ${locale.hasNonBlankValidLanguage} | hasNonBlankValidCountry: $hasNonBlankValidCountry == ${locale.hasNonBlankValidCountry})") {
      locale.isValid shouldBe isValid
      locale.hasNonBlankValidLanguage shouldBe hasNonBlankValidLanguage
      locale.hasNonBlankValidCountry shouldBe hasNonBlankValidCountry
    }
  }

  private def checkLanguageHelper(locales: Locale*)(f: Locale => Boolean): Unit = {
    locales.foreach{ locale: Locale =>
      f(locale) shouldBe true
    }
  }

}