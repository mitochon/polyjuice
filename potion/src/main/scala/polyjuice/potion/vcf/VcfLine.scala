package polyjuice.potion.vcf

import polyjuice.potion.model._

case class VcfLine(
  chrom: String,
  pos: Int,
  ref: String,
  alt: String,
  info: Map[InfoKey, String] = Map(),
  id: Option[String] = None,
  qual: Option[Float] = None,
  filter: Option[String] = None) {

  override def toString: String = {
    val idStr = id.getOrElse(VcfLine.Missing)
    val filterStr = filter.getOrElse(VcfLine.Missing)
    val qualStr = qual.map(q => s"$q").getOrElse(VcfLine.Missing)
    val infoStr = info.map(kv => s"${kv._1.id}=${kv._2}").mkString(";")

    Seq(chrom, s"$pos", idStr, ref, alt, qualStr, filterStr, infoStr).mkString("\t")
  }
}

object VcfLine {

  val Missing = "."

  val GTFormatKey = FormatKey("GT", Count(1), DataType.StringType, "Genotype")

  val TranscriptKey = InfoKey("TR", Count(1), DataType.StringType, "Transcript")

  def apply(variant: VariantCoord, transcript: Option[Transcript]): VcfLine = {
    val infoMap = transcript.map(t => Map((TranscriptKey -> t))).getOrElse(Map())

    def alleleStr(b: Option[Base]): String = b.map(_.toString).getOrElse(Missing)

    variant match {
      case Snv(c, p, r, a)     => VcfLine(c, p, r.toString, a.toString, infoMap)
      case Mnv(c, p, r, a)     => VcfLine(c, p, r.mkString, a.mkString, infoMap)
      case Ins(c, p, r, a)     => VcfLine(c, p, alleleStr(r), a.mkString, infoMap)
      case Del(c, p, r, a)     => VcfLine(c, p, r.mkString, alleleStr(a), infoMap)
      case Complex(c, p, r, a) => VcfLine(c, p, r.mkString, a.mkString, infoMap)
    }
  }

  def printVcf(
    lines: Seq[VcfLine],
    addChrPrefix: Boolean = false,
    metaKeys: Seq[MetaKey] = Seq(),
    fileFormat: Option[FileFormatKey] = None): Seq[String] = {

    val infoKeys = lines.flatMap(_.info.keySet).toSet

    val body = if (addChrPrefix) lines.map(l => s"chr$l") else lines.map(l => s"$l")

    fileFormat.getOrElse(VcfHeader.FileFormat43).toString +:
      (infoKeys.map(_.toString).toSeq ++
        metaKeys.map(_.toString) ++
        Seq(VcfHeader.HeaderLineNoSample) ++
        body)
  }
}
