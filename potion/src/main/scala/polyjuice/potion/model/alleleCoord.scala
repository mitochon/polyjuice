package polyjuice.potion.model

sealed trait AlleleCoord {
  def contig: String
  def pos: Int
}

sealed trait VariantCoord extends AlleleCoord

case class Single(
  contig: String,
  pos: Int,
  base: Base) extends AlleleCoord

case class Triple(
  contig: String,
  pos: Int,
  bases: Codon,
  break: Option[CodonBreak])
  extends AlleleCoord

case class Snv(
  contig: String,
  pos: Int,
  ref: Base,
  alt: Base) extends VariantCoord

case class Ins(
  contig: String,
  pos: Int,
  ref: Option[Base],
  alt: Seq[Base]) extends VariantCoord {

  require(alt.nonEmpty && ref.exists(_ == alt(0)))
}

case class Del(
  contig: String,
  pos: Int,
  ref: Seq[Base],
  alt: Option[Base]) extends VariantCoord {

  require(ref.nonEmpty && alt.exists(_ == ref(0)))
}

case class Mnv(
  contig: String,
  pos: Int,
  ref: Seq[Base],
  alt: Seq[Base]) extends VariantCoord {

  require(ref.nonEmpty && alt.nonEmpty && ref.length == alt.length)
}

case class Complex(
  contig: String,
  pos: Int,
  ref: Seq[Base],
  alt: Seq[Base]) extends VariantCoord {

  require(ref.nonEmpty && alt.nonEmpty && ref.length != alt.length)
}

object VariantCoord {

  // Note: this ordering will NOT produce a stable sort
  object OrderingByBaseLength extends Ordering[VariantCoord] {

    override def compare(a: VariantCoord, b: VariantCoord) = {
      (a, b) match {
        case (s1: Snv, s2: Snv)         => 0
        case (m1: Mnv, m2: Mnv)         => Ordering[Int].compare(m1.ref.size, m2.ref.size)
        case (i1: Ins, i2: Ins)         => Ordering[Int].compare(i1.alt.size, i2.alt.size)
        case (d1: Del, d2: Del)         => Ordering[Int].compare(d1.ref.size, d2.ref.size)
        case (c1: Complex, c2: Complex) => Ordering[Int].compare(c1.ref.size + c1.alt.size, c2.ref.size + c2.alt.size)
        case (s: Snv, _)                => -1 // prioritize snv first
        case (_, s: Snv)                => 1
        case (m: Mnv, _)                => -1 // then mnv
        case (_, m: Mnv)                => 1
        case _                          => 0
      }
    }
  }
}