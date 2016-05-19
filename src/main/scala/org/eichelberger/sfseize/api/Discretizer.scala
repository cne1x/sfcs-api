package org.eichelberger.sfseize.api

abstract class Discretizer[T : Ordering] extends DiscreteSource {
  def baseName: String = ""

  def name: String = "UnnamedDiscretizer"

  def discretize(data: T): Long

  def undiscretize(bin: Long): Space[T]
}
