package org.eichelberger.sfseize.api

/******************************************************
  *  discretizers/cells
  ******************************************************/

// okay, so these aren't just operations on "continuous" spaces,
// but also on linear/uniform distributions of continuous values...

case class ContinuousFieldRange(minimum: Double, maximum: Double, override val isMaximumInclusive: Boolean = false)
  extends FieldRange[Double]

case class ContinuousSpace(ranges: Seq[ContinuousFieldRange]) extends Space[Double]

case class ContinuousDiscretizer(override val name: String, range: ContinuousFieldRange, cardinality: Long) extends Discretizer[Double] {
  val rangeSize = range.maximum - range.minimum
  val binSize = rangeSize / cardinality.toDouble

  def conditionedDatum(data: Double): Double =
    Math.min(Math.max(data, range.minimum), range.maximum)

  def conditionedBin(bin: Long): Long =
    Math.min(Math.max(bin, 0), cardinality)

  // should return a bin index on [0, c-1]
  def discretize(data: Double): Long = {
    Math.min(cardinality - 1, Math.floor((conditionedDatum(data) - range.minimum) / binSize).toLong)
  }

  // should return a range of [min, max)
  def undiscretize(rawBin: Long): Space[Double] = {
    val bin = conditionedBin(rawBin)
    ContinuousSpace(
      Seq(
        ContinuousFieldRange(
          range.minimum + bin * binSize,
          range.minimum + (bin + 1.0) * binSize,
          isMaximumInclusive = bin == cardinality - 1
        )
      )
    )
  }
}

/******************************************************
  *  single curve
  ******************************************************/

case class RowMajorCurve(children: Seq[DiscreteSource]) extends Curve {
  override def baseName: String = "RowMajorCurve"

  def placeValues: Seq[Long] =
    (for (i <- 1 until numChildren) yield cardinalities.slice(i, numChildren).product) ++ Seq(1L)

  override def encode(point: Seq[Long]): Long =
    point.zip(placeValues).foldLeft(0L)((acc, t) => t match {
      case (coordinate, placeValue) => acc + coordinate * placeValue
    })

  override def decode(index: Long): Seq[Long] = {
    (0 until numChildren).foldLeft((index, Seq[Long]()))((acc, i) => acc match {
      case (remainder, seqSoFar) =>
        val value = remainder / placeValues(i)
        (remainder - value * placeValues(i), seqSoFar ++ Seq(value))
    })
  }._2
}
