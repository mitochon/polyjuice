package polyjuice.potion.model

case class EnsemblGene(
  geneSymbol: GeneSymbol,
  transcript: Transcript,
  chr: String,
  start: Int,
  end: Int,
  strand: Strand.Value,
  utr5: Option[UTR5],
  utr3: Option[UTR3],
  exons: IndexedSeq[Exon],
  codingSequence: String)