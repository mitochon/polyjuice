package polyjuice.potion.vcf

trait VcfHeader

object VcfHeader {

  val IdRegex = """[A-Za-z_][0-9A-Za-z_.]""".r

  val FileFormat43 = FileFormatKey("4.3")

  val HeaderLineNoSample = Seq("#CHROM", "POS", "ID", "REF", "ALT", "QUAL", "FILTER", "INFO").mkString("\t")
}