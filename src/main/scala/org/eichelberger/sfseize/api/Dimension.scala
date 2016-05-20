package org.eichelberger.sfseize.api

abstract class FieldRange[T : Ordering] {
  def minimum: T

  def isMinimumInclusive: Boolean = true

  def maximum: T

  def isMaximumInclusive: Boolean = false
}

abstract class Dimension[T : Ordering] extends FieldRange[T] with DiscreteSource {
}
