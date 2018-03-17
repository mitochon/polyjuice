package polyjuice.potion.parser

import scala.util.Try

import polyjuice.potion.model._

object CNameParser {

  // e.g. c.123G>A
  val rCdsSub = """(?:c\.)?([\d]+)([ACTG])>([ACTG])""".r
  // e.g. c.12_13del
  val rCdsDel = """(?:c\.)?([\d]+)_([\d]+)del""".r
  // e.g. c.12_13dup
  val rCdsDup = """(?:c\.)?([\d]+)_([\d]+)dup""".r
  // e.g. c.12del
  val rCdsDelSingle = """(?:c\.)?([\d]+)del""".r
  // e.g. c.12dup
  val rCdsDupSingle = """(?:c\.)?([\d]+)dup""".r
  // e.g. c.12_13inv
  val rCdsInv = """(?:c\.)?([\d]+)_([\d]+)inv""".r
  // e.g. c.12_13insCTA
  val rCdsIns = """(?:c\.)?([\d]+)_([\d]+)ins([ACTGN]+)""".r
  // e.g. c.12_13delinsAG
  val rCdsDelIns = """(?:c\.)?([\d]+)_([\d]+)delins([ACTGN]+)""".r
  // e.g. c.12delinsAG
  val rCdsDelInsSingle = """(?:c\.)?([\d]+)delins([ACTGN]+)""".r

  def cdsSub(pos: String, base1: String, base2: String): Option[CdsSub] = {
    (for {
      p <- Try(pos.toInt)
      b1 <- Try(Base(base1.head))
      b2 <- Try(Base(base2.head))
    } yield CdsSub(p, b1, b2)).toOption
  }

  def cdsDel(start: String, end: Option[String]): Option[CdsDel] = {
    (for {
      s <- Try(start.toInt)
      e <- Try(end.map(_.toInt))
      if (e.isEmpty || e.exists(_ > s))
    } yield CdsDel(s, e)).toOption
  }

  def cdsDup(start: String, end: Option[String]): Option[CdsDup] = {
    (for {
      s <- Try(start.toInt)
      e <- Try(end.map(_.toInt))
      if (e.isEmpty || e.exists(_ > s))
    } yield CdsDup(s, e)).toOption
  }

  def cdsInv(start: String, end: String): Option[CdsInv] = {
    (for {
      s <- Try(start.toInt)
      e <- Try(end.toInt)
      if (s < e)
    } yield CdsInv(s, e)).toOption
  }

  def cdsIns(start: String, end: String, bases: String): Option[CdsIns] = {
    (for {
      s <- Try(start.toInt)
      e <- Try(end.toInt)
      b <- Try(bases.map(Base(_)))
      if (s < e)
    } yield CdsIns(s, e, b)).toOption
  }

  def cdsDelIns(start: String, end: Option[String], bases: String): Option[CdsDelIns] = {
    (for {
      s <- Try(start.toInt)
      e <- Try(end.map(_.toInt))
      b <- Try(bases.map(Base(_)))
      if (e.isEmpty || e.exists(_ > s))
    } yield CdsDelIns(s, e, b)).toOption
  }

  def parse(hgvs: String): Option[CdsVariant] = {
    hgvs match {
      case rCdsSub(p, b1, b2)     => cdsSub(p, b1, b2)
      case rCdsDel(s, e)          => cdsDel(s, Some(e))
      case rCdsDup(s, e)          => cdsDup(s, Some(e))
      case rCdsDelSingle(s)       => cdsDel(s, None)
      case rCdsDupSingle(s)       => cdsDup(s, None)
      case rCdsInv(s, e)          => cdsInv(s, e)
      case rCdsIns(s, e, b)       => cdsIns(s, e, b)
      case rCdsDelIns(s, e, b)    => cdsDelIns(s, Some(e), b)
      case rCdsDelInsSingle(s, b) => cdsDelIns(s, None, b)
      case _                      => None
    }
  }
}
