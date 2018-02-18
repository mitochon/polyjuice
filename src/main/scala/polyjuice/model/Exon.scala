package polyjuice.model

import scala.util.Try

case class Exon(
  ensemblId: String,
  transcript: String,
  rank: Short,
  chr: String,
  start: Int,
  end: Int,
  phase: Option[CodonPhase.Value],
  endPhase: Option[CodonPhase.Value])

object Exon {

  val AttrId = "exon_id"
  val AttrRank = "rank"
  val AttrPhase = "ensembl_phase"
  val AttrEndPhase = "ensembl_end_phase"

  @throws[Exception]
  def apply(record: EnsemblGff3Record): Exon = {

    def getPhase(attr: String) =
      for {
        a <- record.attributes.get(attr)
        p <- Try(CodonPhase.withName(a)).toOption
      } yield p

    Exon(
      record.attributes.get(EnsemblGff3Record.AttrName).get,
      record.getParentTranscript.get,
      record.attributes.get(AttrRank).map(_.toShort).get,
      record.seqId,
      record.start,
      record.end,
      getPhase(AttrPhase),
      getPhase(AttrEndPhase))
  }
}