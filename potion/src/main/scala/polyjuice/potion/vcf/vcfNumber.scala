package polyjuice.potion.vcf

import scala.util.Try

sealed trait VcfNumber

case class Count(n: Int) extends VcfNumber {
  override val toString: String = s"$n"
}

case object Alt extends VcfNumber {
  override val toString: String = "A"
}

case object RefAlt extends VcfNumber {
  override val toString: String = "R"
}

case object Genotype extends VcfNumber {
  override val toString: String = "G"
}

case object Unbounded extends VcfNumber {
  override val toString: String = "."
}

object VcfNumber {

  @throws[Exception]
  def apply(number: String): VcfNumber = {
    Try(number.toInt).toOption.filter(_ >= 0).map(Count)
      .getOrElse(
        number match {
          case Alt.toString       => Alt
          case RefAlt.toString    => RefAlt
          case Genotype.toString  => Genotype
          case Unbounded.toString => Unbounded
          case _                  => throw new IllegalArgumentException("Bad vcf number " + number)
        })
  }
}