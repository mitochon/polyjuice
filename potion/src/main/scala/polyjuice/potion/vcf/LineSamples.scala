package polyjuice.potion.vcf

case class LineSamples(keys: Seq[FormatKey], samples: Seq[Sample]) {

  require( // if GT is defined it needs to be the first key
    (for {
      gt <- keys.map(_.id).find(_.equals(VcfLine.GTFormatKey.id))
      fst <- keys.headOption.map(_.id)
    } yield gt.equals(fst)).getOrElse(true))
}