package fm.common.jshelpers

import scala.scalajs.js

// Similar to js.UndefOr, except it also handles null's
@js.native
sealed trait UndefNullOr[+A] extends js.Any

object UndefNullOr {
  implicit class UndefNullOrOps[+A](val self: UndefNullOr[A]) extends AnyVal {
    def toOption: Option[A] = {
      self match {
        case v if js.isUndefined(v) => None
        case v if v.isNull          => None
        case v: A                   => Option(v).asInstanceOf[Option[A]]
      }
    }
  }
}