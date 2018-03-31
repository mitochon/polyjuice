package polyjuice.potion.model

case class EnsemblGene(
  geneSymbol: GeneSymbol,
  transcript: Transcript,
  chr: String,
  start: Int,
  end: Int,
  strand: Strand.Value,
  utr5: IndexedSeq[UTR5],
  utr3: IndexedSeq[UTR3],
  exons: IndexedSeq[Exon],
  codingSequence: String) {

  def utr5Len: Int = utr5.map(_.length).sum
  def utr3Len: Int = utr3.map(_.length).sum
}