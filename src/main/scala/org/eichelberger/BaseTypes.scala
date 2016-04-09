package org.eichelberger

import org.joda.time.DateTime

sealed trait FieldType
case object FieldTypeOrdinalNumber extends FieldType
case object FieldTypeContinuousNumber extends FieldType
case object FieldTypeDate extends FieldType

object BaseTypes {
  type OrdinalNumber = Long

  type ContinuousNumber = Double

  type Date = DateTime
}
