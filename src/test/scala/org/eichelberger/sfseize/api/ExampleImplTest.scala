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
      println(s"Field range:  $fieldRange")
      fieldRange must not(beNull)
    }
  }

  "longitude ContinuousDiscretizer" should {
    "correctly handle extreme value" in {
      for (c <- 1 to 36) {
        val discretizer = ContinuousDiscretizer("example", ContinuousFieldRange(-180.0, 180.0), c)

        val binMax = discretizer.discretize(180.0)
        println(s"Discretizing maximum longitude, $c bins:  $binMax")
        binMax must equalTo(c - 1)

        val binMin = discretizer.discretize(-180.0)
        println(s"Discretizing minimum longitude, $c bins:  $binMin")
        binMin must equalTo(0)
      }

      1 must equalTo(1)
    }
  }
}
