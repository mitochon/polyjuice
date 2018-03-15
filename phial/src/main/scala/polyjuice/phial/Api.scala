package polyjuice.phial

import polyjuice.potion.model._
import polyjuice.potion.tracer._

case class Api(genes: Map[GeneSymbol, Gene]) {

  def getGene(g: GeneSymbol): Option[Gene] = {
    genes.get(g.toUpperCase)
  }

  def cdsPos(g: GeneSymbol, pos: Int): Option[Map[Transcript, Base]] = {
    genes.get(g.toUpperCase).map(CodingSequenceTracer(_).cds(pos))
  }

  def codonPos(g: GeneSymbol, pos: Int): Option[Map[Transcript, Codon]] = {
    genes.get(g.toUpperCase).map(CodonTracer(_).codon(pos))
  }

  def exonNum(g: GeneSymbol, num: Int): Option[Map[Transcript, Exon]] = {
    None // TODO
  }
}