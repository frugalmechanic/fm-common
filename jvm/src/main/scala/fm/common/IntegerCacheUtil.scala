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
 * A helper for determining the java.lang.Integer.IntegerCache.high value (controlled by -XX:AutoBoxCacheMax)
 *
 * Originally from fm-serializer
 */
protected[common] object IntegerCacheUtil {
  val low: Int = {
    System.getProperty("fm.common.IntegerCache.low").toIntOption.getOrElse{ -128 }
  }

  // Note: Ideally we would use the java.lang.Integer.IntegerCache.high property to get this value but the JVM removes
  //       that property from public access.  So we use brute force to determine the value instead
  //
  // Original code that does not work:
  //    System.getProperty("java.lang.Integer.IntegerCache.high").toIntOption.getOrElse(127)
  val high: Int = {
    System.getProperty("fm.common.IntegerCache.high").toIntOption.getOrElse{ determineIntegerCacheHighValue() }
  }

  require(low <= high, s"Expected low ($low) to be <= high ($high)")

  // Use brute force to determine the value of the private system property "java.lang.Integer.IntegerCache.high"
  private def determineIntegerCacheHighValue(): Int = {
    // The java.lang.Integer.IntegerCache is hardcoded to not be lower than this value
    val IntegerCacheMinValue: Int = 127

    // The maximum value we are willing to go up to.  Since this implementation is somewhat
    // dependant of the behavior of OpenJDK let's set an upper limit to avoid out of memory
    // errors (or other unexpected behaviors) if the JVM we are running in does something else
    // for the caching of Integers.  For Example:  If a JVM used a WeakHashMap for integer caching
    // then `Integer.valueOf(i) eq Integer.valueOf(i)` would always be true.
    val IntegerCacheMaxValue: Int = 1000000

    var i: Int = IntegerCacheMinValue

    while (i < IntegerCacheMaxValue) {
      if (Integer.valueOf(i) ne Integer.valueOf(i)) return i - 1
      i += 1
    }

    IntegerCacheMaxValue
  }
}
