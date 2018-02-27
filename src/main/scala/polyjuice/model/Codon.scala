package polyjuice.model

case class Codon(first: Base, second: Base, third: Base) {
  def bases: String = s"$first$second$third"
  def toSeq: Seq[Base] = Seq(first, second, third)
}

object Codon {

  val Start = Codon("ATG")
  val Stops = Seq(Codon("TAA"), Codon("TAG"), Codon("TGA"))

  @throws[Exception]
  def apply(bases: String): Codon = {
    bases.map(Base(_)) match {
      case Seq(f, s, t) => Codon(f, s, t)
      case _            => throw new IllegalArgumentException("Incorrect number of bases")
    }
  }
}