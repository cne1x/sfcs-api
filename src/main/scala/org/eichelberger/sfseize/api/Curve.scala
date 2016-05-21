package org.eichelberger.sfseize.api

trait Curve extends DiscreteSource {
  require(accepts(cardinalities), s"This curve, $name, does not accept these cardinalities:  " +
    cardinalities.mkString("[", ", ", "]"))

  def children: Seq[DiscreteSource]

  // MUST be overridden, or it will reject all cardinalities
  def accepts(cardinalities: Seq[Long]): Boolean

  def encode(point: Seq[Long]): Long

  def decode(index: Long): Seq[Long]

  def numChildren: Int = children.size

  def cardinalities: Seq[Long] = children.map(_.cardinality)

  def cardinality: Long = cardinalities.product

  def isSquare: Boolean = cardinalities.size match {
    case 0 | 1 => true
    case _     =>
      cardinalities.tail.forall(_ == cardinalities.head)
  }

  def baseName: String = "Curve"

  def name: String = baseName + children.map(_.name).mkString("(", ", ", ")")
}

object Curve {
  def acceptNonZero(cardinality: Long): Boolean = cardinality > 0

  def acceptMultipleOf(cardinality: Long, factor: Long): Boolean = (cardinality % factor) == 0

  def acceptPowerOf(cardinality: Long, base: Long): Boolean = {
    // skip the expensive operations, if you can
    if (cardinality <= 1) return false

    // a bit gross, really... find a better way?
    val power = Math.round(Math.log(cardinality) / Math.log(base)).toLong
    Math.round(Math.pow(base, power)) == cardinality
  }
}