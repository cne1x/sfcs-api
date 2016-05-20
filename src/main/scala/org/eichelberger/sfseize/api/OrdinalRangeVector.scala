package org.eichelberger.sfseize.api

case class OrdinalRangeVector(ranges: Seq[OrdinalRange]) {
  def totalSize = ranges.map(_.size).sum
}
