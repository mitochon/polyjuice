package polyjuice.potion.model

sealed trait ProteinVariant

case class ProteinSub(
  pos: Int,
  from: AminoAcid.Code.Value,
  to: AminoAcid.Code.Value) extends ProteinVariant

case class ProteinDel(
  start: Int,
  startAa: AminoAcid.Code.Value,
  end: Option[Int],
  endAa: Option[AminoAcid.Code.Value]) extends ProteinVariant

case class ProteinDup(
  start: Int,
  startAa: AminoAcid.Code.Value,
  end: Option[Int],
  endAa: Option[AminoAcid.Code.Value]) extends ProteinVariant

case class ProteinIns(
  start: Int,
  startAa: AminoAcid.Code.Value,
  end: Int,
  endAa: AminoAcid.Code.Value,
  inserted: Seq[AminoAcid.Code.Value]) extends ProteinVariant

case class ProteinDelIns(
  start: Int,
  startAa: AminoAcid.Code.Value,
  end: Option[Int],
  endAa: Option[AminoAcid.Code.Value],
  inserted: Seq[AminoAcid.Code.Value]) extends ProteinVariant
