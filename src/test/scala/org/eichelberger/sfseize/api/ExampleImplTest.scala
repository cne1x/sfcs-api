package org.eichelberger.sfseize.api

import com.typesafe.scalalogging.LazyLogging
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.mutable.Specification

@RunWith(classOf[JUnitRunner])
class ExampleImplTest extends Specification with LazyLogging {
  "FieldRange" should {
    "be creatable" in {
      val fieldRange = ContinuousFieldRange(0.0, 1.0)
      logger.debug(s"Field range:  $fieldRange")
      fieldRange must not(beNull)
    }
  }

  "longitude ContinuousDiscretizer" should {
    "correctly handle extreme value" in {
      for (cardinality <- 1 to 36) {
        val discretizer = ContinuousDiscretizer("example", ContinuousFieldRange(-180.0, 180.0), cardinality)

        val binMax = discretizer.discretize(180.0)
        logger.debug(s"Discretizing maximum longitude, $cardinality bins:  $binMax")
        binMax must equalTo(cardinality - 1)

        val binMin = discretizer.discretize(-180.0)
        logger.debug(s"Discretizing minimum longitude, $cardinality bins:  $binMin")
        binMin must equalTo(0)
      }

      1 must equalTo(1)
    }

    "yield spaces that contain the original values" in {
      for (cardinality <- 1 to 36) {
        val discretizer = ContinuousDiscretizer("example", ContinuousFieldRange(-180.0, 180.0), cardinality)

        logger.debug(s"Testing space containment for undiscretizing... $cardinality")

        for (longitude <- -180.0 to 180.0 by 0.1) {
          val bin = discretizer.discretize(longitude)
          val space = discretizer.undiscretize(bin)
          val contains = space.contains(Seq(longitude))
          if (!contains)
            logger.error(s"Undiscretize longitude, $cardinality bins, $longitude degrees -> space $space -> $contains")
          contains must beTrue
        }
      }

      1 must equalTo(1)
    }
  }

  // generic method that doesn't care about the odering; it
  // just needs to be able to map each index to a unique point
  def indexToPoint(cardinalities: Seq[Long], index: Long): Seq[Long] = {
    val numChildren = cardinalities.length
    val placeValues: Seq[Long] =
      (for (i <- 1 until numChildren) yield cardinalities.slice(i, numChildren).product) ++ Seq(1L)
    (0 until numChildren).foldLeft((index, Seq[Long]()))((acc, i) => acc match {
      case (remainder, seqSoFar) =>
        val value = remainder / placeValues(i)
        (remainder - value * placeValues(i), seqSoFar ++ Seq(value))
    })._2
  }

  def validateCurve(curve: Curve) = {
    val indexes = new collection.mutable.HashSet[Long]()

    for (i <- 0 until curve.cardinality.toInt) {
      val point = indexToPoint(curve.cardinalities, i.toLong)
      val index = curve.encode(point)
      val returnPoint = curve.decode(index)
      logger.debug(s"${curve.name}.encode lcv $i -> (${point.mkString(",")}) = $index -> $returnPoint")

      // no index seen more than once
      indexes.contains(index) must beFalse
      indexes.add(index)

      // index must be invertible
      returnPoint.size must equalTo(curve.numChildren)
      (0 until curve.numChildren).foreach(i => returnPoint(i) must equalTo(point(i)))
    }

    // all valid indexes must be seen
    for (i <- 0 until curve.cardinality.toInt) {
      if (!indexes.contains(i)) println(s"Failed to find index $i")
      indexes.contains(i) must beTrue
    }

    // you must have computed no index not already checked
    indexes.size must equalTo(curve.cardinality)
  }

  "row-major curve" should {
    "cover all index values without repeats" in {
      val xDisc = ContinuousDiscretizer("x", ContinuousFieldRange(0.0, 1.0), 5)
      val yDisc = ContinuousDiscretizer("y", ContinuousFieldRange(0.0, 1.0), 2)
      val zDisc = ContinuousDiscretizer("z", ContinuousFieldRange(0.0, 1.0), 3)
      val r = RowMajorCurve(Seq(xDisc, yDisc, zDisc))

      validateCurve(r)
    }
  }

  "square Peano 2D curve" should {
    "encode" in {
      val disc = ContinuousDiscretizer("w", ContinuousFieldRange(0.0, 1.0), 9)
      val p = PeanoCurve2D(Seq(disc, disc))

      validateCurve(p)
    }
  }
}
