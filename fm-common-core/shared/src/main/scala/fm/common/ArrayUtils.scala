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

import scala.reflect.ClassTag

object ArrayUtils {
  def permutations[T: ClassTag](values: Iterable[Iterable[T]]): IndexedSeq[IndexedSeq[T]] = {
    val arg: Array[Array[T]] = values.toArray.map{ _.toArray }
    
    ImmutableArray.unsafeWrapArray(permutations(arg)).map{ ImmutableArray.unsafeWrapArray }
  }
  
  /**
   * Return all permutations of values of an array of arrays
   * 
   * e.g.:
   * scala> permutations(Array(Array(1,2,3), Array(1,2,3), Array(1,2))).map{ _.mkString(",") }.foreach{println}
   *   1,1,1
   *   1,1,2
   *   1,2,1
   *   1,2,2
   *   1,3,1
   *   1,3,2
   *   2,1,1
   *   2,1,2
   *   2,2,1
   *   2,2,2
   *   2,3,1
   *   2,3,2
   *   3,1,1
   *   3,1,2
   *   3,2,1
   *   3,2,2
   *   3,3,1
   *   3,3,2
   *   
   *   
   *   Retain Ordering = false:
   *   
   *   1,1,1
   *   2,1,1
   *   3,1,1
   *   1,2,1
   *   2,2,1
   *   3,2,1
   *   1,3,1
   *   2,3,1
   *   3,3,1
   *   1,1,2
   *   2,1,2
   *   3,1,2
   *   1,2,2
   *   2,2,2
   *   3,2,2
   *   1,3,2
   *   2,3,2
   *   3,3,2
   * 
   * http://stackoverflow.com/questions/5751091/permutations-of-an-array-of-arrays-of-strings
   */
  def permutations[T: scala.reflect.ClassTag](values: Array[Array[T]]): Array[Array[T]] = {
    val results: Array[Array[T]] = new Array(values.map{ _.length }.product)
    
    val retainOrdering: Boolean = true
    
    var resultIdx: Int = 0
    
    while(resultIdx < results.length) {
      val result: Array[T] = new Array(values.length)
      
      var num: Int = resultIdx
      
      if (retainOrdering) {
        var i: Int = values.length - 1
        while (i >= 0) {
          val remainder: Int = num % values(i).length
          num = (num - remainder) / values(i).length
          result(i) = values(i)(remainder)
          i -= 1
        }
      } else {
        var i: Int = 0
        while (i < values.length) {
          val remainder: Int = num % values(i).length
          num = (num - remainder) / values(i).length
          result(i) = values(i)(remainder)
          i += 1
        }
      }
      
      results(resultIdx) = result
      resultIdx += 1
    }
    
    results
  }

  def shingles[T: ClassTag](parts: Array[T]): ImmutableArray[ImmutableArray[T]] = {
    shingles(ImmutableArray.unsafeWrapArray(parts), 1, Int.MaxValue, true)
  }

  def shingles[T: ClassTag](parts: IndexedSeq[T]): ImmutableArray[ImmutableArray[T]] = {
    shingles(parts, 1, Int.MaxValue, true)
  }

  def shingles[T: ClassTag](parts: IndexedSeq[T], minShingleSize: Int, maxShingleSize: Int, forceIncludeOriginal: Boolean): ImmutableArray[ImmutableArray[T]] = {
    require(minShingleSize >= 1, "minShingleSize must be >= 1 but got: "+minShingleSize)
    require(maxShingleSize >= 1, "maxShingleSize must be >= 1 but got: "+maxShingleSize)
    require(minShingleSize <= maxShingleSize, s"minShingleSize <= maxShingleSize but got minShingleSize: $minShingleSize and maxShingleSize: $maxShingleSize")

    if (null == parts || parts.isEmpty) return ImmutableArray.empty[ImmutableArray[T]]

    val b = ImmutableArray.newBuilder[ImmutableArray[T]]

    if (forceIncludeOriginal && maxShingleSize < parts.length) b += ImmutableArray.unsafeWrapArray(parts.toArray)

    var i: Int = 0

    while (i < parts.size) {
      var j: Int = minShingleSize

      val maxSize: Int = math.min(parts.size - i, maxShingleSize)

      while (j <= maxSize) {
        val dst: Array[T] = new Array[T](j)
        var x: Int = 0

        while (x < j) {
          dst(x) = parts(x + i)
          x += 1
        }

        b += ImmutableArray.unsafeWrapArray(dst)
        j += 1
      }

      i += 1
    }

    b.result()
  }
}
