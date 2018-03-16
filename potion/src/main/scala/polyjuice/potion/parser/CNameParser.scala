package polyjuice.potion.parser

import scala.util.Try

import polyjuice.potion.model._

object CNameParser {

  // e.g. c.123G>A
  val rCdsSub = """(?:c\.)?([\d]+)([ACTG])>([ACTG])""".r

  def cdsSub(pos: String, base1: String, base2: String): Option[CdsSub] = {
    (for {
      p <- Try(pos.toInt)
      b1 <- Try(Base(base1.head))
      b2 <- Try(Base(base2.head))
    } yield CdsSub(p, b1, b2)).toOption
  }

  def parse(hgvs: String): Option[CdsVariant] = {
    hgvs match {
      case rCdsSub(p, b1, b2) => cdsSub(p, b1, b2)
      case _                  => None
    }
  }
}
