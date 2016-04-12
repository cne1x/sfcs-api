package org.eichelberger.sfseize.api

abstract class Discretizer[T : Ordering] extends Cardinality {
  def fieldName: String

  def discretize(data: T): Long

  def undiscretize(bin: Long): Space[T]
}
