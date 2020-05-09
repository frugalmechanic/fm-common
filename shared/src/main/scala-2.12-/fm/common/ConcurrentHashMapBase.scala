package fm.common

private[common] trait ConcurrentHashMapBase[A, B] extends scala.collection.mutable.Map[A,B] {
  protected def addOneImpl(kv: (A, B)): this.type
  protected def subtractOneImpl(key: A): this.type

  override def +=(kv: (A, B)): this.type = addOneImpl(kv)
  override def -=(k: A): this.type = subtractOneImpl(k)
}