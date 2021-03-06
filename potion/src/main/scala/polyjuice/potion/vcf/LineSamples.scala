package polyjuice.potion.vcf

case class LineSamples(keys: Seq[FormatKey], samples: Seq[Sample]) {

  require( // if GT is defined it needs to be the first key
    (for {
      gt <- keys.map(_.id).find(_.equals(VcfLine.GTFormatKey.id))
      fst <- keys.headOption.map(_.id)
    } yield gt.equals(fst)).getOrElse(true))

  def toLineSeq: Seq[String] = {
    keys.map(_.id).mkString(VcfLine.FormatSeparator) +:
      samples.map(_.toLineBlock(keys))
  }
}

object LineSamples {

  def buildKey(fields: Map[FormatKey, String]): Seq[FormatKey] = {
    fields.keySet.toSeq.sortBy(!_.id.equals(VcfLine.GTFormatKey.id))
  }
}
