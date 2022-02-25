package fm.common.rich

import fm.common.{GrowableCompat, TraversableOnce}

final class RichGrowable[A](val self: GrowableCompat.Growable[A]) extends AnyVal {
  def addAll(xs: TraversableOnce[A]): self.type = {
    xs.foreach{ self += _ }
    self
  }

  @inline final def ++= (xs: TraversableOnce[A]): self.type = addAll(xs)
}
