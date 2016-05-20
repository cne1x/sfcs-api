package org.eichelberger.sfseize.api

case class OrdinalRange(min: Long, max: Long) {
  require(min <= max, s"Invalid ordering for OrdinalRange:  $min > $max")

  def size: Long = max - min + 1L
}