package polyjuice.tracer

import polyjuice.model._

case class Offset(
  exon: Exon,
  pos: Int,
  strand: Strand.Value = Strand.Plus,
  exonDistance: Option[Int] = None) {

  val coordPos = strand match {
    case Strand.Plus  => exon.start + pos - 1
    case Strand.Minus => exon.end - pos + 1
  }

  def codonBreak: Option[CodonBreak] = {
    val delta = strand match {
      case Strand.Plus  => exon.end - coordPos
      case Strand.Minus => coordPos - exon.start
    }

    for {
      d <- exonDistance
      if (delta < 2)
    } yield delta match {
      case 0 => SplitAtFirst(d)
      case _ => SplitAtSecond(d)
    }
  }

  def single(b: Base): Single = {
    Single(exon.chr, coordPos, b)
  }

  def triple(c: Codon): Triple = {
    Triple(exon.chr, coordPos, c, codonBreak)
  }
}
