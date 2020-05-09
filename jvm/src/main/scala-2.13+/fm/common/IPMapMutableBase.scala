package fm.common

import scala.collection.mutable

private[common] trait IPMapMutableBase[T] extends mutable.Builder[(IPOrSubnet,T), IPMapImmutable[T]] {
  def +=(ip: IPOrSubnet, value: T): this.type

  override final def addOne(ipAndValue: (IPOrSubnet, T)): this.type = +=(ipAndValue._1, ipAndValue._2)
}