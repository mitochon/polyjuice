package polyjuice.phial

import polyjuice.potion.model._
import polyjuice.potion.tracer._

case class Api(genes: Map[GeneSymbol, Gene]) {

  val transcripts = genes.foldLeft(Map[Transcript, GeneSymbol]())(addGeneTranscripts)

  def addGeneTranscripts(
    map: Map[Transcript, GeneSymbol],
    entry: (GeneSymbol, Gene)): Map[Transcript, GeneSymbol] = {

    def addTranscript(g: GeneSymbol)(
      m: Map[Transcript, GeneSymbol],
      e: (Transcript, EnsemblGene)): Map[Transcript, GeneSymbol] = {
      m + (e._1 -> g)
    }

    val (gs, gene) = entry
    gene.foldLeft(map)(addTranscript(gs))
  }

  def getGene(g: GeneSymbol): Option[Gene] = {
    genes.get(g.toUpperCase)
  }

  def getTranscript(t: Transcript): Option[Gene] = {
    transcripts.get(t).flatMap(getGene)
  }

  def cdsPos(g: GeneSymbol, pos: Int): Option[Map[Transcript, Base]] = {
    genes.get(g.toUpperCase).map(CodingSequenceTracer(_).cds(pos))
  }

  def codonPos(g: GeneSymbol, pos: Int): Option[Map[Transcript, Codon]] = {
    genes.get(g.toUpperCase).map(CodonTracer(_).codon(pos))
  }

  def exonNum(g: GeneSymbol, num: Int): Option[Map[Transcript, Exon]] = {
    genes.get(g.toUpperCase).map(ExonTracer(_).exon(num))
  }

  def cdsPosTranscript(t: Transcript, pos: Int): Option[Base] = {
    for {
      g <- transcripts.get(t)
      m <- cdsPos(g, pos)
      b <- m.get(t)
    } yield b
  }

  def codonPosTranscript(t: Transcript, pos: Int): Option[Codon] = {
    for {
      g <- transcripts.get(t)
      m <- codonPos(g, pos)
      c <- m.get(t)
    } yield c
  }

  def exonNumTranscript(t: Transcript, num: Int): Option[Exon] = {
    for {
      g <- transcripts.get(t)
      m <- exonNum(g, num)
      e <- m.get(t)
    } yield e
  }
}