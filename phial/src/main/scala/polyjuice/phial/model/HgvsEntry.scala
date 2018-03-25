package polyjuice.phial.model

import polyjuice.potion.model._

case class HgvsEntry(gene: Option[GeneSymbol], transcript: Option[Transcript], hgvs: String) {

  def toVcfMetaValue: String = {
    transcript.orElse(gene).getOrElse("") + s"_${hgvs.stripPrefix("p.").stripPrefix("c.")}"
  }
}

object HgvsEntry {

  val IsPName = (hgvs: String) => hgvs.startsWith("p.")
  val IsCName = (hgvs: String) => hgvs.startsWith("c.")

  // partition by 1st criteria, 2nd criteria, and remaining
  type PartitionedEntries = (Seq[HgvsEntry], Seq[HgvsEntry], Seq[HgvsEntry])

  def partitionByHgvsType(xs: Seq[HgvsEntry]): PartitionedEntries = {
    val (pEntries, other) = xs.partition(e => HgvsEntry.IsPName(e.hgvs))
    val (cEntries, missing) = other.partition(e => HgvsEntry.IsCName(e.hgvs))
    (pEntries, cEntries, missing)
  }

  def partitionByGeneOrTranscript(xs: Seq[HgvsEntry]): PartitionedEntries = {
    val (byGene, other) = xs.partition(_.gene.isDefined)
    val (byTranscript, missing) = other.partition(_.transcript.isDefined)
    (byGene, byTranscript, missing)
  }
}