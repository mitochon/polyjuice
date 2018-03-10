package polyjuice.tracer

import polyjuice.model._

object VariantBuilder {

  def snv(single: Single, to: Base, strand: Strand.Value = Strand.Plus): Snv = {
    strand match {
      case Strand.Plus  => Snv(single.contig, single.pos, single.base, to)
      case Strand.Minus => Snv(single.contig, single.pos, single.base, to.complement)
    }
  }

  def build(triple: Triple, codon: Codon, strand: Strand.Value): Option[VariantCoord] = {

    val target = strand match {
      case Strand.Plus  => codon
      case Strand.Minus => codon.flip
    }

    val (f, s, t) = diff(triple.bases, target)

    def isAfterBreak(basePick: Codon => Base, break: CodonBreak): Boolean = {
      break.offset match {
        case 1 => basePick == Codon.GetSecond || basePick == Codon.GetThird
        case 2 => basePick == Codon.GetThird
      }
    }

    def snv(basePick: Codon => Base, break: Option[CodonBreak]): Snv = {
      val o = break.map(_.offset).getOrElse(0)
      val d = break.filter(isAfterBreak(basePick, _)).map(_.distance).getOrElse(0)
      Snv(triple.contig, triple.pos + o + d, basePick(triple.bases), basePick(target))
    }

    def mnv(basePick: Codon => Seq[Base], break: Option[CodonBreak]): Option[Mnv] = {
      if (break.isDefined && basePick == Codon.GetAll ||
        break.map(_.offset).exists(_ == 1 && basePick == Codon.GetFirstTwo) ||
        break.map(_.offset).exists(_ == 2 && basePick == Codon.GetLastTwo)) {
        None
      } else {
        val o = break.map(_.offset).getOrElse(0)
        val d = break.filter(_ => basePick == Codon.GetLastTwo).map(_.distance).getOrElse(0)
        Some(Mnv(triple.contig, triple.pos + o + d, basePick(triple.bases), basePick(target)))
      }
    }

    diff(triple.bases, target) match {
      // 1-base change
      case (1, 0, 0) => Some(snv(Codon.GetFirst, triple.break))
      case (0, 1, 0) => Some(snv(Codon.GetSecond, triple.break))
      case (0, 0, 1) => Some(snv(Codon.GetThird, triple.break))
      // 2-base change
      case (1, 1, 0) => mnv(Codon.GetFirstTwo, triple.break)
      case (0, 1, 1) => mnv(Codon.GetLastTwo, triple.break)
      // 3-base change
      case (1, 0, 1) => mnv(Codon.GetAll, triple.break)
      case (1, 1, 1) => mnv(Codon.GetAll, triple.break)
      case _         => None
    }
  }

  def diff(from: Codon, to: Codon): (Int, Int, Int) = {
    def toInt(b: Boolean): Int = if (b) 1 else 0
    (
      toInt(from.first != to.first),
      toInt(from.second != to.second),
      toInt(from.third != to.third))
  }
}