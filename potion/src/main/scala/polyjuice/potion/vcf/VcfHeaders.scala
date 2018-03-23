package polyjuice.potion.vcf

object VcfHeaders {

  val Version = "##fileformat=VCFv4.3"

  val HeaderNoSample = Seq("#CHROM", "POS", "ID", "REF", "ALT", "QUAL", "FILTER", "INFO").mkString("\t")
}