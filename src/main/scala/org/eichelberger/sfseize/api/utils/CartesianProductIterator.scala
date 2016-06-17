package org.eichelberger.sfseize.api.utils

case class CartesianProductIterator(cardinalities: Seq[Long]) {
  var index = 0L

  val placeValues = cardinalities.reverse.foldLeft((1L, Seq[Long]()))((acc, cardinality) => acc match {
    case (placeValue, seqSoFar) =>
      (placeValue * cardinality, Seq(placeValue) ++ seqSoFar)
  })._2

  val maxIndex = cardinalities.product - 1

  def hasNext: Boolean = index <= maxIndex

  def next: Seq[Long] = {
    val result = placeValues.foldLeft((index, Seq[Long]()))((acc, placeValue) => acc match {
      case (remainder, seqSoFar) =>
        val digit = remainder / placeValue
        val nextRemainder = remainder % placeValue
        (nextRemainder, seqSoFar :+ digit)
    })._2
    index += 1L
    result
  }
}
