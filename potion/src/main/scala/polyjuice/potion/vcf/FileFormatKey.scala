package polyjuice.potion.vcf

case class FileFormatKey(version: String) extends VcfHeader {

  override def toString: String = s"##fileformat=VCFv$version"
}
