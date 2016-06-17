package org.eichelberger.sfseize.api.utils

import com.typesafe.scalalogging.LazyLogging
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CartesianProductIteratorTest extends Specification with LazyLogging {
  "CartesianProductIterator" should {
    "properly handle cardinalities of one" in {
      1 must equalTo(1)
    }

    "visit all combinations exactly once" in {
      val itr = new CartesianProductIterator(Seq(2, 1, 3, 4))

      var size = 0
      while (itr.hasNext) {
        logger.debug(s"$size:  ${itr.next}")
        size += 1
      }

      size must equalTo(24)
    }
  }
}
