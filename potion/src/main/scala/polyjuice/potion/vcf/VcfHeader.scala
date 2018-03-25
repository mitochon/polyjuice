package polyjuice.potion.vcf

trait VcfHeader

object VcfHeader {

  val FileFormatLine = "##fileformat=VCFv4.3"

  val HeaderLineNoSample = Seq("#CHROM", "POS", "ID", "REF", "ALT", "QUAL", "FILTER", "INFO").mkString("\t")
}