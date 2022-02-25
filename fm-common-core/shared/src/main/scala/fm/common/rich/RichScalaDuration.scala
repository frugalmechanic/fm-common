package fm.common.rich

import java.time.Duration
import scala.concurrent.duration.FiniteDuration

final class RichScalaDuration(val self: FiniteDuration) extends AnyVal {
  def asJava: Duration = Duration.of(self.length, self.unit.toChronoUnit)
}
