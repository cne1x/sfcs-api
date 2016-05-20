package org.eichelberger.sfseize.api

/**
  * Given a shape, this trait enables a space-filling curve to identify the
  * minimum set of contiguous index ranges that cover that shape, subject to
  * a collection of hints that can color how precise the result is allowed
  * to be.
  */
trait RangeFinder {
  // a RangeFinder is only useful when mixed in with a space-filling curve of some sort
  this: Curve =>

  def getRanges(shapeToCover: IndexShape): OrdinalRangeVector
}
