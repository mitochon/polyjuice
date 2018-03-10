package polyjuice.model

case class Codon(first: Base, second: Base, third: Base) {
  def bases: String = s"$first$second$third"

  def toSeq: Seq[Base] = {
    Seq(first, second, third)
  }

  def flip: Codon = {
    Codon(third.complement, second.complement, first.complement)
  }
}

object Codon {

  val Start = Codon("ATG")
  val Stops = Seq(Codon("TAA"), Codon("TAG"), Codon("TGA"))

  val GetFirst = (c: Codon) => c.first
  val GetSecond = (c: Codon) => c.second
  val GetThird = (c: Codon) => c.third
  val GetFirstTwo = (c: Codon) => c.toSeq.take(2)
  val GetLastTwo = (c: Codon) => c.toSeq.drop(1)
  val GetAll = (c: Codon) => c.toSeq

  @throws[Exception]
  def apply(bases: String): Codon = {
    bases.map(Base(_)) match {
      case Seq(f, s, t) => Codon(f, s, t)
      case _            => throw new IllegalArgumentException("Incorrect number of bases")
    }
  }
}