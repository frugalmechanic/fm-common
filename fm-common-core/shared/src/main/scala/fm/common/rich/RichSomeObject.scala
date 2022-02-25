package fm.common.rich

import fm.common.OptionCache

final class RichSomeObject(val module: Some.type) extends AnyVal {

  /** Returns a cached copy of the Option instance (if available) to avoid allocation */
  def cached(v: Boolean): Some[Boolean] = OptionCache.valueOf(v)

  /** Returns a cached copy of the Option instance (if available) to avoid allocation */
  def cached(v: Byte): Some[Byte] = OptionCache.valueOf(v)

  /** Returns a cached copy of the Option instance (if available) to avoid allocation */
  def cached(v: Char): Some[Char] = OptionCache.valueOf(v)

  /** Returns a cached copy of the Option instance (if available) to avoid allocation */
  def cached(v: Short): Some[Short] = OptionCache.valueOf(v)

  /** Returns a cached copy of the Option instance (if available) to avoid allocation */
  def cached(v: Int): Some[Int] = OptionCache.valueOf(v)

  /** Returns a cached copy of the Option instance (if available) to avoid allocation */
  def cached(v: Long): Some[Long] = OptionCache.valueOf(v)
}
