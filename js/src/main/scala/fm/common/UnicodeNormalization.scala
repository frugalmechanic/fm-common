/*
 * Copyright 2019 Frugal Mechanic (http://frugalmechanic.com)
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

import scala.scalajs.js.{JSStringOps, UnicodeNormalizationForm}

// Needs to match the JVM Version
object UnicodeNormalization extends UnicodeNormalizationBase {
  import JSStringOps.enableJSStringOps
  
  def normalizeNFC(s: String): String = s.normalize(UnicodeNormalizationForm.NFC)
  def normalizeNFD(s: String): String = s.normalize(UnicodeNormalizationForm.NFD)
  def normalizeNFKC(s: String): String = s.normalize(UnicodeNormalizationForm.NFKC)
  def normalizeNFKD(s: String): String = s.normalize(UnicodeNormalizationForm.NFKD)
}
