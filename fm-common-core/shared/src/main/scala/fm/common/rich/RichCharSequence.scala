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

import fm.common.{ASCIIUtil, ImmutableArray, ImmutableArrayBuilder}

/**
 * Provides additional functionality for java.lang.CharSequence
 */
final class RichCharSequence(val s: CharSequence) extends AnyVal {

  /**
   * Returns true if the string is null or only whitespace
   *
   */
  def isNullOrBlank: Boolean = {
    if (null == s) return true
    
    var i: Int = 0
    val len: Int = s.length()
    while (i < len) {
      if (!Character.isWhitespace(s.charAt(i))) return false 
      i += 1
    }
    
    true
  }

  /**
   * Opposite of isBlank
   */
  def isNotNullOrBlank: Boolean = !isNullOrBlank
  
  /**
   * Opposite of isBlank (alias for isNotBlank)
   */
  def nonNullOrBlank: Boolean = !isNullOrBlank

  /**
   * Do the next characters starting at idx match the target
   */
  def nextCharsMatch(target: CharSequence, idx: Int = 0): Boolean = {
    if (idx < 0) throw new IllegalArgumentException(s"RichSequence.nextCharsMatch - Negative Idx: $idx")
    if (null == target || target.length == 0) return false

    var i: Int = 0
    while (i < target.length && i+idx < s.length && target.charAt(i) == s.charAt(i+idx)) {
      i += 1
    }

    i == target.length
  }

  /**
   * Same as String.startsWith(prefix) but for a CharSequence
   */
  def startsWith(target: CharSequence): Boolean = nextCharsMatch(target)
  
  /**
   * Count the occurrences of the character
   */
  def countOccurrences(ch: Char): Int = {
    var count: Int = 0
    var i: Int = 0
    
    while (i < s.length) {
      if (s.charAt(i) == ch) count += 1
      i += 1
    }
    
    count
  }
  
  def indexesOf(target: CharSequence, withOverlaps: Boolean): IndexedSeq[Int] = indexesOf(target, 0, withOverlaps)
  
  def indexesOf(target: CharSequence, fromIdx: Int, withOverlaps: Boolean): IndexedSeq[Int] = {
    if (target == null) return ImmutableArray.empty[Int]
    
    val builder = new ImmutableArrayBuilder[Int](0)
    
    var i: Int = fromIdx
    
    while (i < s.length) {
      if (nextCharsMatch(target, i)) {
        builder += i
        i += (if (withOverlaps) 1 else target.length)
      } else {
        i += 1
      }
    }
   
    builder.result()
  }
  
  def matches(pattern: java.util.regex.Pattern): Boolean = pattern.matcher(s).matches()
  def matches(regex: scala.util.matching.Regex): Boolean = regex.pattern.matcher(s).matches()

  def containsNormalized(target: CharSequence): Boolean = {
    containsWithTransform(target, Character.isLetterOrDigit(_), (c: Char) => Character.toLowerCase(ASCIIUtil.toASCIIChar(c)))
  }

  def containsIgnoreCase(target: CharSequence): Boolean = {
    containsWithTransform(target, (_: Char) => true, (c: Char) => Character.toLowerCase(c))
  }

  @inline def containsWithTransform(target: CharSequence, filter: Char => Boolean, map: Char => Char): Boolean = {
    indexOfWithTransform(target, filter, map) > -1
  }

  def indexOfNormalized(target: CharSequence): Int = {
    indexOfWithTransform(target, Character.isLetterOrDigit(_), (c: Char) => Character.toLowerCase(ASCIIUtil.toASCIIChar(c)))
  }

  def indexOfIgnoreCase(target: CharSequence): Int = {
    indexOfWithTransform(target, (_: Char) => true, (c: Char) => Character.toLowerCase(c))
  }

  @inline def indexOfWithTransform(target: CharSequence, filter: Char => Boolean, map: Char => Char): Int = {
    if (null == s || null == target) return -1
    if (target.length == 0) return 0

    //
    // Skip past any chars that we do not care about in the target
    //
    var targetStartingIdx: Int = 0
    while (targetStartingIdx < target.length && !filter(target.charAt(targetStartingIdx))) targetStartingIdx += 1

    //
    // Loop over the source and check for matches of the target
    //
    var sourceIdx: Int = 0

    while (sourceIdx < s.length) {
      // Only check starting from this index if the char is included in our filter (so we have an accurate startIdx
      // that can be returned from indexOfWithTransformImpl)
      if (filter(s.charAt(sourceIdx))) {
        val res: Int = indexOfWithTransformImpl(target, targetStartingIdx, sourceIdx, filter, map)
        if (-1 != res) return res
      }

      sourceIdx += 1
    }

    -1
  }

  @inline private def indexOfWithTransformImpl(target: CharSequence, targetStartIdx: Int, sourceStartIdx: Int, filter: Char => Boolean, map: Char => Char): Int = {
    var targetIdx: Int = targetStartIdx
    var sourceIdx: Int = sourceStartIdx

    // Was originally a "do <body> while <cond>" which was removed in Scala 3 so this was converted to the recommended
    // "while ({ <body> ; <cond> }) ()" equivalent: https://docs.scala-lang.org/scala3/reference/dropped-features/do-while.html
    while ({
      //
      // 1 - Check if the chars match.
      //
      // Note: At this point we have already filtered past bad chars either in the indexOfWithTransform method
      //       or via the code below.
      //
      val sourceChar: Char = map(s.charAt(sourceIdx))
      val targetChar: Char = map(target.charAt(targetIdx))

      if (sourceChar != targetChar) return -1

      //
      // 2 - Advance our indexes
      //
      targetIdx += 1
      sourceIdx += 1

      //
      // 3 - Skip past any chars we do not care about
      //
      while (targetIdx < target.length && !filter(target.charAt(targetIdx))) targetIdx += 1
      while (sourceIdx < s.length && !filter(s.charAt(sourceIdx))) sourceIdx += 1

      targetIdx < target.length && sourceIdx < s.length
    }) ()

    // If we have reached the end of the target then we have matches everything
    if (targetIdx == target.length) sourceStartIdx else -1
  }

  def equalsNormalized(target: CharSequence): Boolean = {
    equalsWithTransform(target, Character.isLetterOrDigit(_), (c: Char) => Character.toLowerCase(ASCIIUtil.toASCIIChar(c)))
  }

  @inline def equalsWithTransform(target: CharSequence, filter: Char => Boolean, map: Char => Char): Boolean = {
    if (null == s || null == target) return false

    var sourceIdx: Int = 0
    var targetIdx: Int = 0

    while (sourceIdx < s.length && targetIdx < target.length) {
      //
      // 1 - Skip past any chars we do not care about
      //
      while (targetIdx < target.length && !filter(target.charAt(targetIdx))) targetIdx += 1
      while (sourceIdx < s.length && !filter(s.charAt(sourceIdx))) sourceIdx += 1

      if (sourceIdx < s.length && targetIdx < target.length) {
        //
        // 2 - Check if the chars match
        //
        val sourceChar: Char = map(s.charAt(sourceIdx))
        val targetChar: Char = map(target.charAt(targetIdx))

        if (sourceChar != targetChar) return false

        //
        // 3 - Advance our indexes
        //
        targetIdx += 1
        sourceIdx += 1
      }
    }

    // Skip over any remaining chars that should be filtered out
    while (targetIdx < target.length && !filter(target.charAt(targetIdx))) targetIdx += 1
    while (sourceIdx < s.length && !filter(s.charAt(sourceIdx))) sourceIdx += 1

    // It is a match if we are at the end of both strings
    sourceIdx == s.length && targetIdx == target.length
  }
}