package fm.common.jshelpers

import scala.scalajs.js

// Similar to js.UndefOr, except handles converting nulls into Options
@js.native
sealed trait NullOr[+A] extends js.Any

object NullOr {
  implicit class NullOrOps[+A](val self: NullOr[A]) extends AnyVal {
    def toOption: Option[A] = Option(self).asInstanceOf[Option[A]]
  }
}