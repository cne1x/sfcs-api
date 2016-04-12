package org.eichelberger.sfseize.api

/******************************************************
  *  discretizers/cells
  ******************************************************/

// okay, so these aren't just operations on "continuous" spaces,
// but also on linear/uniform distributions of continuous values...

case class ContinuousFieldRange(minimum: Double, maximum: Double)
  extends FieldRange[Double]

case class ContinuousSpace(ranges: Seq[ContinuousFieldRange]) extends Space[Double]

case class ContinuousDiscretizer(fieldName: String, range: ContinuousFieldRange, cardinality: Long) extends Discretizer[Double] {
  val rangeSize = range.maximum - range.minimum
  val binSize = rangeSize / cardinality.toDouble

  def conditioned(data: Double): Double =
    Math.min(Math.max(data, range.minimum), range.maximum)

  // should return a bin index on [0..c-1]
  def discretize(data: Double): Long = {
    Math.min(cardinality - 1, Math.floor((conditioned(data) - range.minimum) / binSize).toLong)
  }

  def undiscretize(bin: Long): Space[Double] = {
    ???
  }
}