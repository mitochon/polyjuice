package polyjuice.model

sealed trait AlleleCoord {
  def contig: String
  def pos: Int
}

sealed trait VariantCoord extends AlleleCoord

case class Single(contig: String, pos: Int, base: Base) extends AlleleCoord

case class Snv(contig: String, pos: Int, ref: Base, alt: Base) extends VariantCoord

case class Ins(contig: String, pos: Int, ref: Option[Base], alt: Seq[Base]) extends VariantCoord {
  require(alt.nonEmpty && ref.exists(_ == alt(0)))
}

case class Del(contig: String, pos: Int, ref: Seq[Base], alt: Option[Base]) extends VariantCoord {
  require(ref.nonEmpty && alt.exists(_ == ref(0)))
}

case class Mnp(contig: String, pos: Int, ref: Seq[Base], alt: Seq[Base]) extends VariantCoord {
  require(ref.nonEmpty && alt.nonEmpty && ref.length == alt.length)
}

case class Clumped(contig: String, pos: Int, ref: Seq[Base], alt: Seq[Base]) extends VariantCoord {
  require(ref.nonEmpty && alt.nonEmpty && ref.length != alt.length)
}