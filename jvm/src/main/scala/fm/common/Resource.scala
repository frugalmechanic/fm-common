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

import java.io.IOException

object Resource {
  private[this] val logger = Logger.getLogger(this) 
  
  def using[T, R <% AutoCloseable](resource: R)(f: R => T): T = try {
    f(resource)
  } catch {
    case ex: Exception =>
      logger.debug("Caught exception using resource", ex)
      throw ex
  } finally {
    if (null != resource) resource.close()
  }
  
  def using[T, R <% AutoCloseable](resources: Seq[R])(f: Seq[R] => T): T = try {
    f(resources)
  } catch {
    case ex: Exception =>
      logger.debug("Caught exception using resources", ex)
      throw ex
  } finally {
    if (null != resources) resources.foreach{r => if(null != r) r.close() }
  }
  
  implicit def toCloseable[T <: { def close(): Unit }](obj: T): AutoCloseable = new AutoCloseable {
    import scala.language.reflectiveCalls
    
    // This causes a reflective call
    def close(): Unit = obj.close()
  }
  
  def apply[T <: AutoCloseable](resource: T): Resource[T] = toResource(resource)
  
  implicit def toResource[T <: AutoCloseable](resource: T): Resource[T] = SingleUseResource(resource)
  
  val empty: Resource[Unit] = UnitResource
  
  //
  // Helpers for using multiple AutoCloseables
  //
  // Generated using:
  //
  //  (1 to 21).map{ i: Int =>
  //    val letters = ('a' to ('a'+i).toChar).toVector
  //    val tpeParams: String = letters.map{ _.toUpper+" <% AutoCloseable" }.mkString(", ")
  //    val params: String = letters.map{ ch: Char => s"$ch: ${ch.toUpper}" }.mkString(", ")
  //    val funParams: String = letters.map{ _.toUpper }.mkString(", ")
  //    val useParams: String = letters.map{ ch: Char => s"SingleUseResource($ch)" }.mkString(", ")
  //    s"def using[RES, $tpeParams]($params)(fun: ($funParams) => RES): RES = use($useParams)(fun)"
  //  }.foreach{ println }
  def using[RES, A <% AutoCloseable, B <% AutoCloseable](a: A, b: B)(fun: (A, B) => RES): RES = use(SingleUseResource(a), SingleUseResource(b))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable](a: A, b: B, c: C)(fun: (A, B, C) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable](a: A, b: B, c: C, d: D)(fun: (A, B, C, D) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable](a: A, b: B, c: C, d: D, e: E)(fun: (A, B, C, D, E) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F)(fun: (A, B, C, D, E, F) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G)(fun: (A, B, C, D, E, F, G) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H)(fun: (A, B, C, D, E, F, G, H) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I)(fun: (A, B, C, D, E, F, G, H, I) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J)(fun: (A, B, C, D, E, F, G, H, I, J) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K)(fun: (A, B, C, D, E, F, G, H, I, J, K) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L)(fun: (A, B, C, D, E, F, G, H, I, J, K, L) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable, N <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m), SingleUseResource(n))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable, N <% AutoCloseable, O <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m), SingleUseResource(n), SingleUseResource(o))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable, N <% AutoCloseable, O <% AutoCloseable, P <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m), SingleUseResource(n), SingleUseResource(o), SingleUseResource(p))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable, N <% AutoCloseable, O <% AutoCloseable, P <% AutoCloseable, Q <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m), SingleUseResource(n), SingleUseResource(o), SingleUseResource(p), SingleUseResource(q))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable, N <% AutoCloseable, O <% AutoCloseable, P <% AutoCloseable, Q <% AutoCloseable, R <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m), SingleUseResource(n), SingleUseResource(o), SingleUseResource(p), SingleUseResource(q), SingleUseResource(r))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable, N <% AutoCloseable, O <% AutoCloseable, P <% AutoCloseable, Q <% AutoCloseable, R <% AutoCloseable, S <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m), SingleUseResource(n), SingleUseResource(o), SingleUseResource(p), SingleUseResource(q), SingleUseResource(r), SingleUseResource(s))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable, N <% AutoCloseable, O <% AutoCloseable, P <% AutoCloseable, Q <% AutoCloseable, R <% AutoCloseable, S <% AutoCloseable, T <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m), SingleUseResource(n), SingleUseResource(o), SingleUseResource(p), SingleUseResource(q), SingleUseResource(r), SingleUseResource(s), SingleUseResource(t))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable, N <% AutoCloseable, O <% AutoCloseable, P <% AutoCloseable, Q <% AutoCloseable, R <% AutoCloseable, S <% AutoCloseable, T <% AutoCloseable, U <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m), SingleUseResource(n), SingleUseResource(o), SingleUseResource(p), SingleUseResource(q), SingleUseResource(r), SingleUseResource(s), SingleUseResource(t), SingleUseResource(u))(fun)
  def using[RES, A <% AutoCloseable, B <% AutoCloseable, C <% AutoCloseable, D <% AutoCloseable, E <% AutoCloseable, F <% AutoCloseable, G <% AutoCloseable, H <% AutoCloseable, I <% AutoCloseable, J <% AutoCloseable, K <% AutoCloseable, L <% AutoCloseable, M <% AutoCloseable, N <% AutoCloseable, O <% AutoCloseable, P <% AutoCloseable, Q <% AutoCloseable, R <% AutoCloseable, S <% AutoCloseable, T <% AutoCloseable, U <% AutoCloseable, V <% AutoCloseable](a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V)(fun: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) => RES): RES = use(SingleUseResource(a), SingleUseResource(b), SingleUseResource(c), SingleUseResource(d), SingleUseResource(e), SingleUseResource(f), SingleUseResource(g), SingleUseResource(h), SingleUseResource(i), SingleUseResource(j), SingleUseResource(k), SingleUseResource(l), SingleUseResource(m), SingleUseResource(n), SingleUseResource(o), SingleUseResource(p), SingleUseResource(q), SingleUseResource(r), SingleUseResource(s), SingleUseResource(t), SingleUseResource(u), SingleUseResource(v))(fun)
  
  //
  // Helpers for using multiple resource
  //
  
  def use[RES,A](a: Resource[A])(fun: A => RES): RES = a.use{ aa => fun(aa) }
  def use[RES,A,B](a: Resource[A], b: Resource[B])(fun: (A,B) => RES): RES = a.use{ aa => b.use{ bb => fun(aa,bb) } }
  def use[RES,A,B,C](a: Resource[A], b: Resource[B], c: Resource[C])(fun: (A,B,C) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => fun(aa,bb,cc) } } }
  def use[RES,A,B,C,D](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D])(fun: (A,B,C,D) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => fun(aa,bb,cc,dd) } } } }
  def use[RES,A,B,C,D,E](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E])(fun: (A,B,C,D,E) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => fun(aa,bb,cc,dd,ee) } } } } }
  def use[RES,A,B,C,D,E,F](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F])(fun: (A,B,C,D,E,F) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => fun(aa,bb,cc,dd,ee,ff) } } } } } }
  def use[RES,A,B,C,D,E,F,G](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G])(fun: (A,B,C,D,E,F,G) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => fun(aa,bb,cc,dd,ee,ff,gg) } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H])(fun: (A,B,C,D,E,F,G,H) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => fun(aa,bb,cc,dd,ee,ff,gg,hh) } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I])(fun: (A,B,C,D,E,F,G,H,I) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii) } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J])(fun: (A,B,C,D,E,F,G,H,I,J) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj) } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K])(fun: (A,B,C,D,E,F,G,H,I,J,K) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk) } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L])(fun: (A,B,C,D,E,F,G,H,I,J,K,L) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll) } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm) } } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn) } } } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N], o: Resource[O])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => o.use { oo => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn,oo) } } } } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N], o: Resource[O], p: Resource[P])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => o.use { oo => p.use { pp => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn,oo,pp) } } } } } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N], o: Resource[O], p: Resource[P], q: Resource[Q])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => o.use { oo => p.use { pp => q.use { qq => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn,oo,pp,qq) } } } } } } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N], o: Resource[O], p: Resource[P], q: Resource[Q], r: Resource[R])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => o.use { oo => p.use { pp => q.use { qq => r.use { rr => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn,oo,pp,qq,rr) } } } } } } } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N], o: Resource[O], p: Resource[P], q: Resource[Q], r: Resource[R], s: Resource[S])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => o.use { oo => p.use { pp => q.use { qq => r.use { rr => s.use { ss => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn,oo,pp,qq,rr,ss) } } } } } } } } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N], o: Resource[O], p: Resource[P], q: Resource[Q], r: Resource[R], s: Resource[S], t: Resource[T])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => o.use { oo => p.use { pp => q.use { qq => r.use { rr => s.use { ss => t.use { tt => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn,oo,pp,qq,rr,ss,tt) } } } } } } } } } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N], o: Resource[O], p: Resource[P], q: Resource[Q], r: Resource[R], s: Resource[S], t: Resource[T], u: Resource[U])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => o.use { oo => p.use { pp => q.use { qq => r.use { rr => s.use { ss => t.use { tt => u.use{ uu => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn,oo,pp,qq,rr,ss,tt,uu) } } } } } } } } } } } } } } } } } } } } }
  def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N], o: Resource[O], p: Resource[P], q: Resource[Q], r: Resource[R], s: Resource[S], t: Resource[T], u: Resource[U], v: Resource[V])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => o.use { oo => p.use { pp => q.use { qq => r.use { rr => s.use { ss => t.use { tt => u.use{ uu => v.use { vv => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn,oo,pp,qq,rr,ss,tt,uu,vv) } } } } } } } } } } } } } } } } } } } } } }
  
  // ERROR: type Function23 is not a member of package scala
  //def use[RES,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W](a: Resource[A], b: Resource[B], c: Resource[C], d: Resource[D], e: Resource[E], f: Resource[F], g: Resource[G], h: Resource[H], i: Resource[I], j: Resource[J], k: Resource[K], l: Resource[L], m: Resource[M], n: Resource[N], o: Resource[O], p: Resource[P], q: Resource[Q], r: Resource[R], s: Resource[S], t: Resource[T], u: Resource[U], v: Resource[V], w: Resource[W])(fun: (A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W) => RES): RES = a.use{ aa => b.use{ bb => c.use{ cc => d.use { dd => e.use { ee => f.use{ ff => g.use { gg => h.use{ hh => i.use{ ii => j.use{ jj => k.use { kk => l.use { ll => m.use { mm => n.use{ nn => o.use { oo => p.use { pp => q.use { qq => r.use { rr => s.use { ss => t.use { tt => u.use{ uu => v.use { vv => w.use{ ww => fun(aa,bb,cc,dd,ee,ff,gg,hh,ii,jj,kk,ll,mm,nn,oo,pp,qq,rr,ss,tt,uu,vv,ww) } } } } } } } } } } } } } } } } } } } } } } }
}

/**
 * An Automatically Managed Resource that can either be used once (e.g. reading an input stream) or multiple times (e.g. reading a file).
 * 
 * The purpose of Resource is two-fold:
 * 1 - To automatically handle closing a resource after it is done being used.
 * 2 - To abstract the fact that some resources can be read multiple times while other resources are one-time use.
 */
trait Resource[+A] {
  def use[T](f: A => T): T
  
  /** Is this resource usable?  i.e. will the use() method work? */
  def isUsable: Boolean
  
  /** Can this resource be used multiple times? */
  def isMultiUse: Boolean
  
  final def map[B](f: A => B): Resource[B] = new MappedResource(this, f)
  
  final def flatMap[B](f: A => Resource[B]): Resource[B] = new FlatMappedResource(this, f)
  
  final def foreach[U](f: A => U): Unit = use(f)
}

/**
 * An empty resource
 */
object UnitResource extends Resource[Unit] {
  def use[T](f: Unit => T): T = f(())
  
  def isUsable: Boolean = true
  def isMultiUse: Boolean = true
}

/**
 * A Dummy Resource that does nothing
 */
final case class DummyResource[A](a: A) extends Resource[A] {
  def use[T](f: A => T): T = f(a)
  def isUsable: Boolean = true
  def isMultiUse: Boolean = false
}


object MultiUseResource {
  def apply[A <% AutoCloseable](makeResource: => A) = new MultiUseResource(makeResource)
}

/**
 * A Resource that can be used multiple times (e.g. opening an InputStream or Reader for a File)
 */
final class MultiUseResource[+A <% AutoCloseable](makeResource: => A) extends Resource[A] {
  final def isUsable: Boolean = true
  final def isMultiUse: Boolean = true
  
  final def use[T](f: A => T): T = Resource.using(makeResource)(f)
}

object SingleUseResource {
  def apply[A <% AutoCloseable](resource: A) = new SingleUseResource(resource)
}

/**
 * A Resource that can only be used once (e.g. reading an InputStream)
 */
final class SingleUseResource[+A <% AutoCloseable](resource: A) extends Resource[A] {
  @volatile private[this] var used: Boolean = false
  
  final def isUsable: Boolean = !used
  final def isMultiUse: Boolean = false
  
  final def use[T](f: A => T): T = {
    if(used) throw new IOException("The SingleUseResource has already been used and cannot be used again")
    used = true
    Resource.using(resource)(f)
  }
}

/**
 * For Resource.map
 */
final class MappedResource[A, B](resource: Resource[A], mapping: A => B) extends Resource[B] {
  def use[T](f: B => T): T = resource.use{ a: A => f(mapping(a)) }
  
  def isUsable: Boolean = resource.isUsable
  def isMultiUse: Boolean = resource.isMultiUse
}

/**
 * For Resource.flatMap
 */
final class FlatMappedResource[A, B](resource: Resource[A], mapping: A => Resource[B]) extends Resource[B] {
  def use[T](f: B => T): T = resource.use{ a: A => mapping(a).use[T](f) }
  
  def isUsable: Boolean = resource.isUsable
  def isMultiUse: Boolean = resource.isMultiUse
}