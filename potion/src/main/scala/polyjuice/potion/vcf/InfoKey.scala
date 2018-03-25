package polyjuice.potion.vcf

case class InfoKey(
  id: String,
  number: VcfNumber,
  dataType: DataType.Value,
  description: String,
  source: Option[String] = None,
  version: Option[String] = None) extends VcfHeader {

  require(InfoKey.IdRegex.findFirstIn(id).isDefined)

  override def toString: String = {
    s"##INFO=<ID=$id,Number=$number,Type=${DataType.toString(dataType)}" +
      s""",Description="$description"""" +
      source.map(s => s""",Source="$s"""").getOrElse("") +
      version.map(s => s""",Version="$s"""").getOrElse("") + ">"
  }
}

object InfoKey {

  val IdRegex = """[A-Za-z_][0-9A-Za-z_.]""".r
}
