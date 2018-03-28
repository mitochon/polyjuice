package polyjuice.potion.vcf

case class FormatKey(
  id: String,
  number: VcfNumber,
  dataType: DataType.Value,
  description: String) extends VcfHeader {

  require(VcfHeader.IdRegex.findFirstIn(id).isDefined)

  override def toString: String = {
    s"##INFO=<ID=$id,Number=$number,Type=${DataType.toString(dataType)}" +
      s""",Description="$description">"""
  }
}