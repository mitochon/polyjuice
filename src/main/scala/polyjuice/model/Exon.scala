package polyjuice.model

case class Exon(
  ensemblId: String,
  geneTranscript: String,
  rank: Short,
  chr: String,
  start: Int,
  end: Int,
  phase: CodonPhase.Value,
  endPhase: CodonPhase.Value)

object Exon {

  val AttrId = "exon_id"
  val AttrRank = "rank"
  val AttrPhase = "ensembl_phase"
  val AttrEndPhase = "ensembl_end_phase"

  @throws[Exception]
  def apply(record: EnsemblGff3Record): Exon = {
    Exon(
      record.attributes.get(EnsemblGff3Record.AttrName).get,
      record.getParentTranscript.get,
      record.attributes.get(AttrRank).map(_.toShort).get,
      record.seqId,
      record.start,
      record.end,
      record.attributes.get(AttrPhase).map(CodonPhase.withName).get,
      record.attributes.get(AttrEndPhase).map(CodonPhase.withName).get)
  }
}