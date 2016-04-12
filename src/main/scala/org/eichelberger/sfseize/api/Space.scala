package org.eichelberger.sfseize.api

abstract class Space[T : Ordering] {
  val ordering = implicitly[Ordering[T]]

  def ranges: Seq[FieldRange[T]]

  def contains(data: Seq[T]): Boolean = {
    require(data.size == ranges.size, s"Mismatched sizes:  data ${data.size}, ranges ${ranges.size}")

    data.zip(ranges).forall {
      case (datum, range) =>
        val isMinOk = ordering.gt(datum, range.minimum) || (ordering.gteq(datum, range.minimum) && range.isMinimumInclusive)
        val isMaxOk = ordering.lt(datum, range.maximum) || (ordering.lteq(datum, range.maximum) && range.isMaximumInclusive)
        isMinOk && isMaxOk
    }
  }
}
