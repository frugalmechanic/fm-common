/*
 * Copyright 2018 Frugal Mechanic (http://frugalmechanic.com)
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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

final class TestArrayUtils extends AnyFunSuite with Matchers {
  test("permutations") {
    ArrayUtils.permutations(Array(Array(1,2,3), Array(1,2,3), Array(1,2))) should equal (Array(
      Array(1,1,1),
      Array(1,1,2),
      Array(1,2,1),
      Array(1,2,2),
      Array(1,3,1),
      Array(1,3,2),
      Array(2,1,1),
      Array(2,1,2),
      Array(2,2,1),
      Array(2,2,2),
      Array(2,3,1),
      Array(2,3,2),
      Array(3,1,1),
      Array(3,1,2),
      Array(3,2,1),
      Array(3,2,2),
      Array(3,3,1),
      Array(3,3,2)
    ))
  }

  test("shingles") {
    ArrayUtils.shingles(Array(1,2,3)) should equal (ImmutableArray(
      ImmutableArray(1),
      ImmutableArray(1,2),
      ImmutableArray(1,2,3),
      ImmutableArray(2),
      ImmutableArray(2,3),
      ImmutableArray(3)
    ))
  }

  test("shingles minSize/maxSize == 1, forceIncludeOriginal = false") {
    ArrayUtils.shingles(ImmutableArray(1,2,3), minShingleSize = 1, maxShingleSize = 1, forceIncludeOriginal = false) should equal (ImmutableArray(
      ImmutableArray(1),
      ImmutableArray(2),
      ImmutableArray(3)
    ))
  }

  test("shingles minSize/maxSize == 1, forceIncludeOriginal = true") {
    ArrayUtils.shingles(ImmutableArray(1,2,3), minShingleSize = 1, maxShingleSize = 1, forceIncludeOriginal = true) should equal (ImmutableArray(
      ImmutableArray(1,2,3),
      ImmutableArray(1),
      ImmutableArray(2),
      ImmutableArray(3)
    ))
  }

  test("shingles minSize/maxSize == 2, forceIncludeOriginal = false") {
    ArrayUtils.shingles(ImmutableArray(1,2,3), minShingleSize = 2, maxShingleSize = 2, forceIncludeOriginal = false) should equal (ImmutableArray(
      ImmutableArray(1,2),
      ImmutableArray(2,3)
    ))
  }

  test("shingles minSize/maxSize == 2, forceIncludeOriginal = true") {
    ArrayUtils.shingles(ImmutableArray(1,2,3), minShingleSize = 2, maxShingleSize = 2, forceIncludeOriginal = true) should equal (ImmutableArray(
      ImmutableArray(1,2,3),
      ImmutableArray(1,2),
      ImmutableArray(2,3)
    ))
  }

  test("shingles minSize = 1, maxSize == 2, forceIncludeOriginal = false") {
    ArrayUtils.shingles(ImmutableArray(1,2,3), minShingleSize = 1, maxShingleSize = 2, forceIncludeOriginal = false) should equal (ImmutableArray(
      ImmutableArray(1),
      ImmutableArray(1,2),
      ImmutableArray(2),
      ImmutableArray(2,3),
      ImmutableArray(3)
    ))
  }

  test("shingle minSize = 1, maxSize == 2, forceIncludeOriginal = true") {
    ArrayUtils.shingles(ImmutableArray(1,2,3), minShingleSize = 1, maxShingleSize = 2, forceIncludeOriginal = true) should equal (ImmutableArray(
      ImmutableArray(1,2,3),
      ImmutableArray(1),
      ImmutableArray(1,2),
      ImmutableArray(2),
      ImmutableArray(2,3),
      ImmutableArray(3)
    ))
  }

  test("shingle minSize = 1, maxSize == 3, forceIncludeOriginal = false") {
    ArrayUtils.shingles(ImmutableArray(1,2,3), minShingleSize = 1, maxShingleSize = 3, forceIncludeOriginal = false) should equal (ImmutableArray(
      ImmutableArray(1),
      ImmutableArray(1,2),
      ImmutableArray(1,2,3),
      ImmutableArray(2),
      ImmutableArray(2,3),
      ImmutableArray(3)
    ))
  }

  test("shingles minSize = 1, maxSize == 3, forceIncludeOriginal = true") {
    ArrayUtils.shingles(ImmutableArray(1,2,3), minShingleSize = 1, maxShingleSize = 3, forceIncludeOriginal = true) should equal (ImmutableArray(
      ImmutableArray(1),
      ImmutableArray(1,2),
      ImmutableArray(1,2,3),
      ImmutableArray(2),
      ImmutableArray(2,3),
      ImmutableArray(3)
    ))
  }
}
