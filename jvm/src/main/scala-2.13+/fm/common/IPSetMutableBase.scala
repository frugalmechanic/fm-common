package fm.common

import scala.collection.mutable

private[common] trait IPSetMutableBase extends mutable.Builder[IPOrSubnet, IPSetImmutable] {
  protected def addOneImpl(ip: IPOrSubnet): this.type

  override final def addOne(ip: IPOrSubnet): this.type = addOneImpl(ip)
}