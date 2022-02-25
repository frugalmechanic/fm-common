/*
 * Copyright 2022 Tim Underwood (https://github.com/tpunder)
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

import fm.common.rich._

protected trait ImplicitsCollectionCompat {
  final implicit def toRichMap[A,B,This <: scala.collection.MapLike[A,B,This] with scala.collection.Map[A,B]](m: scala.collection.MapLike[A,B,This]) = new RichMap(m)
}