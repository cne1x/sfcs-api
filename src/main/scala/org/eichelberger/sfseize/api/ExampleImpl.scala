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
  val minimum = range.minimum
  val maximum = range.maximum

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
  *  single curves
  ******************************************************/

case class RowMajorCurve(children: Seq[DiscreteSource]) extends Curve {
  override def baseName: String = "RowMajorCurve"

  // row-major accepts all positive cardinalities
  def accepts(cardinalities: Seq[Long]): Boolean = cardinalities.forall(Curve.acceptNonZero)

  def placeValues: Seq[Long] =
    (for (i <- 1 until numChildren) yield cardinalities.slice(i, numChildren).product) ++ Seq(1L)

  override def encode(point: Seq[Long]): Long =
    point.zip(placeValues).foldLeft(0L)((acc, t) => t match {
      case (coordinate, placeValue) => acc + coordinate * placeValue
    })

  override def decode(index: Long): Seq[Long] =
    (0 until numChildren).foldLeft((index, Seq[Long]()))((acc, i) => acc match {
      case (remainder, seqSoFar) =>
        val value = remainder / placeValues(i)
        (remainder - value * placeValues(i), seqSoFar ++ Seq(value))
    })._2
}

case class PeanoCurve2D(children: Seq[DiscreteSource]) extends Curve {
  override def baseName: String = "PeanoCurve2D"

  // there are only four possible orientations of the 9-square unit;
  //   orientation# -> row-major index
  val orientations = Map(
    0 -> Seq(0, 5, 6, 1, 4, 7, 2, 3, 8),
    1 -> Seq(6, 5, 0, 7, 4, 1, 8, 3, 2),
    2 -> Seq(2, 3, 8, 1, 4, 7, 0, 5, 6),
    3 -> Seq(8, 3, 2, 7, 4, 1, 6, 5, 0)
  )

  // Peano has an easy substitution pattern for orientations
  // as you recurse down levels of detail;
  //   orientation# -> row-major set of orientations used at the next level of precision
  val orientationMap = Map(
    0 -> Seq(0, 2, 0, 1, 3, 1, 0, 2, 0),
    1 -> Seq(1, 3, 1, 0, 2, 0, 1, 3, 1),
    2 -> Seq(2, 0, 2, 3, 1, 3, 2, 0, 2),
    3 -> Seq(3, 1, 1, 2, 0, 2, 3, 1, 3)
  )

  // Peano accepts all cardinalities that are powers of 3;
  // for fun, let's also limit it to 2D squares
  def accepts(cardinalities: Seq[Long]): Boolean =
    cardinalities.length == 2 && cardinalities.forall(Curve.acceptPowerOf(_, 3)) && isSquare

  // now many levels of recursion are there
  def levels: Int = Math.round(Math.log(cardinalities.head) / Math.log(3.0)).toInt

  override def encode(point: Seq[Long]): Long = {
    require(point.length == numChildren)

    def seek(p: Seq[Long], orientation: Int, recursesLeft: Int = levels): Long = {
      require(recursesLeft >= 1, s"$name went weird:  recurses left is $recursesLeft")

      if (recursesLeft == 1) {
        // you've bottomed out
        val offset = 3 * p(0).toInt + p(1).toInt
        orientations(orientation)(offset)
      } else {
        // you have further to recurse
        val unitSize: Long = Math.round(Math.pow(3, recursesLeft - 1))
        val thisY = (p(0) / unitSize).toInt
        val thisX = (p(1) / unitSize).toInt
        val nextOrientation = orientationMap(orientation)(thisY * 3 + thisX)
        val nextY = p(0) % unitSize
        val nextX = p(1) % unitSize

        val steps = orientations(orientation)(thisY * 3 + thisX)
        val basis = steps * unitSize * unitSize

        basis + seek(Seq(nextY, nextX), nextOrientation, recursesLeft - 1)
      }
    }

    // the top-level orientation is always #0
    seek(point, 0)
  }

  override def decode(index: Long): Seq[Long] = {
    require(index >= 0L, s"$name.decode($index) underflow")
    require(index < cardinality, s"$name.decode($index) overflow")

    def seek(Y: Long, X: Long, min: Long, orientation: Int, recursesLeft: Int = levels): Seq[Long] = {
      //TODO debug
      println(s"P.decode($index).seek($Y, $X, $min, $orientation, $recursesLeft)...")

      if (recursesLeft == 1) {
        // bottom out
        val steps = index - min
        val offset = orientations(orientation).indexOf(steps)  // TODO expedite
        val y = Y + offset / 3
        val x = X + offset % 3

        //TODO debug
        println(s"  steps=$steps, offset=$offset, y=$y, x=$x")

        Seq(y, x)
      } else {
        // keep recursing
        val unitSize: Long = Math.round(Math.pow(3, recursesLeft - 1))
        val span = index - min
        val steps = span / (unitSize * unitSize)
        val nextMin = min + steps * unitSize * unitSize
        val offset = orientations(orientation).indexOf(steps)  // TODO expedite
        val y = offset / 3
        val x = offset % 3
        val nextOrientation = orientationMap(orientation)(offset)

        seek(Y + y * unitSize, X + x * unitSize, nextMin, nextOrientation, recursesLeft - 1)
      }
    }

    seek(0, 0, 0, 0)
  }
}

/******************************************************
  *  composed curve
  ******************************************************/

// trite R(x, P(y, z))
class ComposedCurve_RP(xDim: ContinuousDiscretizer, yDim: ContinuousDiscretizer, zDim: ContinuousDiscretizer)
  extends RowMajorCurve(Seq(xDim, PeanoCurve2D(Seq(yDim, zDim))))


/******************************************************
  *  single range-finder
  ******************************************************/
