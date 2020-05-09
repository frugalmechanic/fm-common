package fm.common

import scala.collection.{IterableFactory, SeqOps, mutable}

final class QueryParamsBuilder extends QueryParamsBuilderBase {
  override def addOne(param: (String, String)): this.type = { builder += param; this }
}

private[common] trait QueryParamsBase extends SeqOps[(String, String), Seq, QueryParams] {
  protected def params: Seq[(String, String)]

  //
  // SeqOps Implementation:
  //
  override def iterator: Iterator[(String, String)] = params.iterator
  override def apply(idx: Int): (String, String) = params(idx)
  override def length: Int = params.length

  override protected def coll: QueryParams = this.asInstanceOf[QueryParams]
  override def iterableFactory: IterableFactory[Seq] = Seq
  override def toIterable: Iterable[(String, String)] = params

  override def fromSpecific(it: IterableOnce[(String, String)]): QueryParams = {
    val builder: QueryParamsBuilder = new QueryParamsBuilder
    it.iterator.foreach { builder += _ }
    builder.result
  }

  protected[this] override def newSpecificBuilder: mutable.Builder[(String, String), QueryParams] = new QueryParamsBuilder
}