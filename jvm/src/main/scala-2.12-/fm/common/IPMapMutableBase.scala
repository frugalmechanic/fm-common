package fm.common

private[common] trait IPMapMutableBase[T] extends scala.collection.mutable.Builder[(IPOrSubnet,T), IPMapImmutable[T]] {
  def +=(ip: IPOrSubnet, value: T): this.type

  override final def +=(ipAndValue: (IPOrSubnet, T)): this.type = +=(ipAndValue._1, ipAndValue._2)
}