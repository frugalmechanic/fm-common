package fm.common

private[common] trait ConcurrentHashSetBase[A] extends scala.collection.mutable.Set[A] {
  protected def addOneImpl(elem: A): this.type
  protected def subtractOneImpl(elem: A): this.type

  override def addOne(elem: A): this.type = addOneImpl(elem)
  override def subtractOne(elem: A): this.type = subtractOneImpl(elem)
}