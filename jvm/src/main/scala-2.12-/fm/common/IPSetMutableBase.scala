package fm.common

private[common] trait IPSetMutableBase extends scala.collection.mutable.Builder[IPOrSubnet, IPSetImmutable] {
  protected def addOneImpl(ip: IPOrSubnet): this.type

  override final def +=(ip: IPOrSubnet): this.type = addOneImpl(ip)
}