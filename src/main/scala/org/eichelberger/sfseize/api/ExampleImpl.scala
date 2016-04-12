package org.eichelberger.sfseize.api

/******************************************************
  *  discretizers/cells
  ******************************************************/

case class ContinuousFieldRange(minimum: Double, maximum: Double)
  extends FieldRange[Double]

case class ContinuousSpace(ranges: Seq[ContinuousFieldRange]) extends Space[Double]