package polyjuice.model

case class EnsemblGene(
  gene: String,
  transcript: String,
  chr: String,
  start: Int,
  stop: Int,
  strand: Strand.Value,
  utr5: Option[UTR],
  utr3: Option[UTR],
  exons: Seq[Exon],
  codingSequence: String)