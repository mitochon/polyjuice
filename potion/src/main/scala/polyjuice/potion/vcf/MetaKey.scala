package polyjuice.potion.vcf

case class MetaKey(key: String, value: String) extends VcfHeader {

  override def toString: String = {
    s"""##$key="$value""""
  }
}