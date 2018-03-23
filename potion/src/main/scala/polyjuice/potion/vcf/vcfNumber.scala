package polyjuice.potion.vcf

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
