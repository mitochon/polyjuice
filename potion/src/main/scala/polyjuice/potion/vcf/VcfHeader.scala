package polyjuice.potion.vcf

trait VcfHeader

object VcfHeader {

  val IdRegex = """[A-Za-z_][0-9A-Za-z_.]""".r

  val FileFormat43 = FileFormatKey("4.3")

  val HeaderLineNoSample = Seq("#CHROM", "POS", "ID", "REF", "ALT", "QUAL", "FILTER", "INFO")

  def headerLine(numSamples: Int = 0): String = {
    val samplesBlock = numSamples match {
      case n if n > 0 => "FORMAT" +: Range(1, n).map(i => s"SAMPLE$i")
      case _          => Seq()
    }
    (HeaderLineNoSample ++ samplesBlock).mkString("\t")
  }
}
