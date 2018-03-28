package polyjuice.phial.model

case class Hgvs2VcfRequest(
  entries: Seq[HgvsEntry],
  appendFmtFields: Option[Seq[VcfKeyBuilder]],
  appendInfoFields: Option[Seq[VcfKeyBuilder]],
  vcfFileFormat: Option[String],
  oneVariantPerTranscript: Option[Boolean],
  addChrPrefix: Option[Boolean])