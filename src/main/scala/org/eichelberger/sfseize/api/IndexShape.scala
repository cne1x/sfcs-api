package org.eichelberger.sfseize.api

/**
  * A description of a subset of a discretized space.  That is, the
  * shape is a set of indexed cells collected together to form a
  * single, logical unit.
  *
  * The simplest example is an n-dimensional box, in which each single
  * dimension contributes one contiguous range of index values.
  */
trait IndexShape {
  def contains(coordinate: OrdinalVector): Boolean
}
