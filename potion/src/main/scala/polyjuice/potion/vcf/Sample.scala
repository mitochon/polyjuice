package polyjuice.potion.vcf

case class Sample(fields: Map[FormatKey, String]) {
  def toLineBlock(keys: Seq[FormatKey]): String = {
    keys.map(fields.getOrElse(_, VcfLine.Missing)).mkString(VcfLine.FormatSeparator)
  }
}
