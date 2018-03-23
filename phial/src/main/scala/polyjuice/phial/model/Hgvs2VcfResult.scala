package polyjuice.phial.model

import polyjuice.potion.vcf.VcfLine

case class Hgvs2VcfResult(vcfLines: Seq[String], noMatches: Seq[HgvsEntry], badEntries: Seq[HgvsEntry])