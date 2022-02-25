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

/**
 * This is a replacement for the deprecated scala.collection.SeqProxy.
 * 
 * This doesn't implement Seq[A] like the original SeqProxy but instead
 * just provides an implicit conversion from this trait into a Seq[A] which
 * then gives you all the Seq functionality
 */
trait SeqProxy        [A]   { def self: Seq[A]         }
trait IterableProxy   [A]   { def self: Iterable[A]    }
trait TraversableProxy[A]   { def self: Traversable[A] }
trait IndexedSeqProxy [A]   { def self: IndexedSeq[A]  }
trait SetProxy        [A]   { def self: Set[A]         }
trait MapProxy        [K,V] { def self: Map[K,V]       }

object SeqProxy         { implicit def convert[A]  (p: SeqProxy[A])        : Seq[A]         = p.self }
object IterableProxy    { implicit def convert[A]  (p: IterableProxy[A])   : Iterable[A]    = p.self }
object TraversableProxy { implicit def convert[A]  (p: TraversableProxy[A]): Traversable[A] = p.self }
object IndexedSeqProxy  { implicit def convert[A]  (p: IndexedSeqProxy[A]) : IndexedSeq[A]  = p.self }
object SetProxy         { implicit def convert[A]  (p: SetProxy[A])        : Set[A]         = p.self }
object MapProxy         { implicit def convert[K,V](p: MapProxy[K,V])      : Map[K,V]       = p.self }