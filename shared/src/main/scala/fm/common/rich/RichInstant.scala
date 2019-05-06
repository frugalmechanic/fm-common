package fm.common.rich

import java.time.{Instant, LocalTime}
import java.time.temporal.ChronoUnit

final class RichInstant(val instant: Instant) extends AnyVal {
  def < (other: Instant): Boolean = instant.isBefore(other)
  def > (other: Instant): Boolean = instant.isAfter(other)

  def atStartOfDay: Instant = {
    instant.truncatedTo(ChronoUnit.DAYS)
  }

  def atEndOfDay: Instant = {
    // Not supported by JVM?
    //atStartOfDay.`with`(LocalTime.MAX)
    atStartOfDay
      .plus(LocalTime.MAX.getHour, ChronoUnit.HOURS)
      .plus(LocalTime.MAX.getMinute, ChronoUnit.MINUTES)
      .plus(LocalTime.MAX.getSecond, ChronoUnit.SECONDS)
      .plus(LocalTime.MAX.getNano, ChronoUnit.NANOS)
  }
}