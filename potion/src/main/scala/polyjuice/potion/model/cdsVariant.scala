package polyjuice.potion.model

sealed trait CdsVariant

case class CdsSub(pos: Int, from: Base, to: Base) extends CdsVariant

case class CdsDel(start: Int, end: Option[Int]) extends CdsVariant

case class CdsDup(start: Int, end: Option[Int]) extends CdsVariant

case class CdsInv(start: Int, end: Int) extends CdsVariant

case class CdsIns(start: Int, end: Int, bases: Seq[Base]) extends CdsVariant

case class CdsDelIns(start: Int, end: Int, bases: Seq[Base]) extends CdsVariant