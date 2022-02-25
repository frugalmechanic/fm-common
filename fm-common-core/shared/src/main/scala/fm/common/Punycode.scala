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

/**
 * Punycode as defined in From: https://tools.ietf.org/html/rfc3492
 */
object Punycode extends Bootstring(
  //  /*** Bootstring parameters for Punycode ***/
  //
  //  enum { base = 36, tmin = 1, tmax = 26, skew = 38, damp = 700,
  //    initial_bias = 72, initial_n = 0x80, delimiter = 0x2D };
  alphabet = (('a' to 'z') ++ ('0' to '9')).mkString(""),
  tmin = 1,
  tmax = 26,
  skew = 38,
  damp = 700,
  initialBias = 72,
  initialN = 128,
  delimiter = '-'
) {
  assert(base === 36)

  //  /* basic(cp) tests whether cp is a basic code point: */
  //  #define basic(cp) ((punycode_uint)(cp) < 0x80)
  def isBasicCodePoint(codepoint: Int): Boolean = codepoint < 128
}
