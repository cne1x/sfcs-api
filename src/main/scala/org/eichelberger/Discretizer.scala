package org.eichelberger

import org.eichelberger.BaseTypes.OrdinalNumber

/**
  *
  */
trait Discretizer {
  def cardinality: OrdinalNumber

  def fieldName: String

  def fieldType:  FieldType
}
