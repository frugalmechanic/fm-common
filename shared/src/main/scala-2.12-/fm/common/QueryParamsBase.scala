package fm.common

import scala.collection.SeqLike
import scala.collection.mutable.Builder

final class QueryParamsBuilder extends QueryParamsBuilderBase {
  override def +=(param: (String, String)): this.type = { builder += param; this }
}

private[common] trait QueryParamsBase extends Seq[(String, String)] with SeqLike[(String, String), QueryParams]  {
  protected def params: Seq[(String, String)]

  //
  // SeqLike Implementation:
  //
  def iterator: Iterator[(String, String)] = params.iterator
  def apply(idx: Int): (String, String) = params(idx)
  def length: Int = params.length
  protected[this] override def newBuilder: Builder[(String, String), QueryParams] = new QueryParamsBuilder
}