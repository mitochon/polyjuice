package polyjuice.model

case class EnsemblGene(
  gene: String,
  transcript: String,
  chr: String,
  start: Int,
  end: Int,
  strand: Strand.Value,
  utr5: Option[UTR5],
  utr3: Option[UTR3],
  exons: Seq[Exon],
  codingSequence: String)