package polyjuice.tracer

import polyjuice.model._

case class Offset(exon: Exon, pos: Int) {

  val coordStart = exon.start + pos - 1

  def codonBreak: Option[CodonBreak.Value] = {
    exon.end - coordStart match {
      case 0 => Some(CodonBreak._1)
      case 1 => Some(CodonBreak._2)
      case _ => None
    }
  }

  def single(b: Base): Single = {
    Single(exon.chr, coordStart, b)
  }

  def triple(c: Codon): Triple = {
    Triple(exon.chr, coordStart, c, codonBreak)
  }
}
