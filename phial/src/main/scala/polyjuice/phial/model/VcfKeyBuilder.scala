package polyjuice.phial.model

import scala.util.Try

import polyjuice.potion.vcf._

case class VcfKeyBuilder(
  id: String,
  number: String,
  dataType: String,
  description: String,
  value: String) {

  def buildInfoKey: InfoKey = {
    InfoKey(id, vcfNumber, vcfDataType, description)
  }

  def buildFormatKey: FormatKey = {
    FormatKey(id, vcfNumber, vcfDataType, description)
  }

  def vcfNumber: VcfNumber = {
    (for {
      n <- Option(number)
      v <- Try(VcfNumber(n)).toOption
    } yield v).getOrElse(Count(1))
  }

  def vcfDataType: DataType.Value = {
    (for {
      d <- Option(dataType)
      t <- Try(DataType.withName(d)).toOption
    } yield t).getOrElse(DataType.StringType)
  }
}
