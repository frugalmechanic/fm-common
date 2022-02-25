package fm.common.rich

import java.time.Duration
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

final class RichJavaDuration(val self: Duration) extends AnyVal {
  def asScala: FiniteDuration = FiniteDuration(self.toNanos, TimeUnit.NANOSECONDS)
}
