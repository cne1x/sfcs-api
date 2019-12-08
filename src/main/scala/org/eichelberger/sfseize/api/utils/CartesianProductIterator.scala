package org.eichelberger.sfseize.api.utils

/**
 * The /function/ of this case class is to iterate over all combinations of
 * indexes given.  The implementation of this function is handled by a delegate,
 * because GeoMesa has already written this code, so there's no reason not to
 * include it by reference.
 *
 * @param cardinalities are the sizes of each of the dimension indexes, so Seq(2, 5, 1)
 *                      is equivalent to a three-dimensional space divided into 10
 *                      different cells; the iterator will generate a first Seq[Long]
 *                      of (0, 0, 0) then (1, 0, 0), (0, 1, 0), (1, 1, 0), ..., (1, 4, 0).
 */
case class CartesianProductIterator(cardinalities: Seq[Long]) extends Iterator[Seq[Long]] {
  import org.locationtech.geomesa.utils.iterators.{CartesianProductIterable => GeomesaCPI}

  // GeoMesa has this code, but with a more abstract type; bridge it
  val delegate: Iterator[Seq[_]] = {
    val seqs: Seq[Seq[Long]] = cardinalities.map(c => {
      for (i <- 0L until c) yield i
    })
    GeomesaCPI(seqs).iterator
  }

  def hasNext: Boolean = delegate.hasNext

  // does nothing but promote the type
  def next(): Seq[Long] = delegate.next() match {
    case seq: Seq[Long] => seq
  }
}
