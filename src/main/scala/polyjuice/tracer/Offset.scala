package polyjuice.tracer

import polyjuice.model._

case class Offset(
  exon: Exon,
  pos: Int,
  strand: Strand.Value = Strand.Plus,
  toNextExon: Option[Int] = None) {

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
      d <- toNextExon
      if (delta < 2)
    } yield delta match {
      case 0 => SplitAtFirst(d)
      case _ => SplitAtSecond(d)
    }
  }

  def single(b: Base): Single = {
    strand match {
      case Strand.Plus  => Single(exon.chr, coordPos, b)
      case Strand.Minus => Single(exon.chr, coordPos, b.complement)
    }
  }

  def triple(c: Codon): Triple = {
    val break = codonBreak

    def minusCoord: Int = {
      coordPos - 2 - break.map(_.distance - 1).getOrElse(0)
    }

    strand match {
      case Strand.Plus  => Triple(exon.chr, coordPos, c, break)
      case Strand.Minus => Triple(exon.chr, minusCoord, c.flip, break.map(_.flip))
    }
  }
}
