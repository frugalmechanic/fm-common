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

import java.text.Normalizer

// Needs to match the ScalaJS Version
object UnicodeNormalization extends UnicodeNormalizationBase {
  def normalizeNFC(s: String): String = Normalizer.normalize(s, Normalizer.Form.NFC)
  def normalizeNFD(s: String): String = Normalizer.normalize(s, Normalizer.Form.NFD)
  def normalizeNFKC(s: String): String = Normalizer.normalize(s, Normalizer.Form.NFKC)
  def normalizeNFKD(s: String): String = Normalizer.normalize(s, Normalizer.Form.NFKD)
}
