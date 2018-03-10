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

    def snv(basePick: Codon => Base, offset: Int, break: Option[CodonBreak]): Snv = {
      val d = break.filter(b => offset >= b.offset).map(_.distance).getOrElse(0)
      Snv(triple.contig, triple.pos + offset + d, basePick(triple.bases), basePick(target))
    }

    def mnv(basePick: Codon => Seq[Base], offset: Int, break: Option[CodonBreak]): Option[Mnv] = {
      if (break.isDefined && basePick == Codon.GetAll ||
        break.map(_.offset).exists(_ == 1 && basePick == Codon.GetFirstTwo) ||
        break.map(_.offset).exists(_ == 2 && basePick == Codon.GetLastTwo)) {
        None
      } else {
        val d = break.filter(b => offset >= b.offset).map(_.distance).getOrElse(0)
        Some(Mnv(triple.contig, triple.pos + offset + d, basePick(triple.bases), basePick(target)))
      }
    }

    diff(triple.bases, target) match {
      // 1-base change
      case (1, 0, 0) => Some(snv(Codon.GetFirst, 0, triple.break))
      case (0, 1, 0) => Some(snv(Codon.GetSecond, 1, triple.break))
      case (0, 0, 1) => Some(snv(Codon.GetThird, 2, triple.break))
      // 2-base change
      case (1, 1, 0) => mnv(Codon.GetFirstTwo, 0, triple.break)
      case (0, 1, 1) => mnv(Codon.GetLastTwo, 1, triple.break)
      // 3-base change
      case (1, 0, 1) => mnv(Codon.GetAll, 0, triple.break)
      case (1, 1, 1) => mnv(Codon.GetAll, 0, triple.break)
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