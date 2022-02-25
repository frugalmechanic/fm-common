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

final class QueryParamsBuilder extends BuilderCompat[(String, String), QueryParams] {
  private[this] val builder = Vector.newBuilder[(String, String)]
  def addOne(param: (String, String)): this.type = { builder += param; this }
  def ++=(other: QueryParams): this.type = { builder ++= other.toSeq; this }
  def result(): QueryParams = QueryParams(builder.result())
  def clear(): Unit = builder.clear()
}
