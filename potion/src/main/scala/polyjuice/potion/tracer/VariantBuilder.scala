package polyjuice.potion.tracer

import polyjuice.potion.model._

object VariantBuilder {

  val Gen = util.Random

  def genBase: Base = {
    val bases = Array(Base.A, Base.C, Base.T, Base.G)
    bases(Gen.nextInt(4))
  }

  def snv(single: Single, to: Base, strand: Strand.Value = Strand.Plus): Snv = {
    strand match {
      case Strand.Plus  => Snv(single.contig, single.pos, single.base, to)
      case Strand.Minus => Snv(single.contig, single.pos, single.base, to.complement)
    }
  }

  def ins(start: Single, end: Single, bases: Seq[Base], strand: Strand.Value = Strand.Plus): Ins = {
    strand match {
      case Strand.Plus  => Ins(start.contig, start.pos, Some(start.base), start.base +: bases)
      case Strand.Minus => Ins(end.contig, end.pos, Some(end.base), end.base +: Base.flip(bases))
    }
  }

  def del(leftFlank: Single, rightFlank: Single, bases: Seq[Base], strand: Strand.Value = Strand.Plus): Del = {
    strand match {
      case Strand.Plus  => Del(leftFlank.contig, leftFlank.pos, leftFlank.base +: bases, Some(leftFlank.base))
      case Strand.Minus => Del(rightFlank.contig, rightFlank.pos, rightFlank.base +: Base.flip(bases), Some(rightFlank.base))
    }
  }

  def dup(start: Single, end: Single, bases: Seq[Base], strand: Strand.Value = Strand.Plus): Ins = {
    strand match {
      case Strand.Plus  => Ins(end.contig, end.pos, Some(end.base), end.base +: bases)
      case Strand.Minus => Ins(start.contig, start.pos, Some(start.base), start.base +: Base.flip(bases))
    }
  }

  def inv(start: Single, end: Single, bases: Seq[Base], strand: Strand.Value = Strand.Plus): Mnv = {
    strand match {
      case Strand.Plus  => Mnv(start.contig, start.pos, bases, Base.flip(bases))
      case Strand.Minus => Mnv(end.contig, end.pos, Base.flip(bases), bases)
    }
  }

  def delins(start: Single, end: Single, delbases: Seq[Base], insbases: Seq[Base], strand: Strand.Value = Strand.Plus): VariantCoord = {
    if (delbases.length == insbases.length) {
      strand match {
        case Strand.Plus  => Mnv(start.contig, start.pos, delbases, insbases)
        case Strand.Minus => Mnv(end.contig, end.pos, Base.flip(delbases), Base.flip(insbases))
      }
    } else {
      strand match {
        case Strand.Plus  => Complex(start.contig, start.pos, delbases, insbases)
        case Strand.Minus => Complex(end.contig, end.pos, Base.flip(delbases), Base.flip(insbases))
      }
    }
  }

  def build(triple: Triple, codon: Codon, strand: Strand.Value): Option[VariantCoord] = {

    val target = strand match {
      case Strand.Plus  => codon
      case Strand.Minus => codon.flip
    }

    // compare first, second, and third base of the codon
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

  def frameshift(triple: Triple, strand: Strand.Value): VariantCoord = {
    // randomly inserts or 1 to 2 bases or delete the second base in the codon
    if (Gen.nextBoolean()) {
      val bases = List.fill(1 + Gen.nextInt(2))(genBase)
      Ins(triple.contig, triple.pos + 1, Some(triple.bases.second), triple.bases.second +: bases)
    } else {
      Del(triple.contig, triple.pos, triple.bases.toSeq.take(2), Some(triple.bases.first))
    }
  }
}