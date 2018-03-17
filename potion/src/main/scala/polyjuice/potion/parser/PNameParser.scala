package polyjuice.potion.parser

import scala.util.Try

import polyjuice.potion.model._

object PNameParser {

  // e.g. p.Thr790Met
  val rProteinSub = """(?:p\.)?([A-V][a-z][a-z])([\d]+)([A-V][a-z][a-z])""".r

  // e.g. p.T790M
  val rProteinSubLetter = """(?:p\.)?([A-Y])([\d]+)([A-Y])""".r

  def proteinSubLetter(pos: String, l1: String, l2: String): Option[ProteinSub] = {
    for {
      p <- Try(pos.toInt).toOption
      aa1 <- AminoAcid.BySingleLetter.get(l1.head)
      aa2 <- AminoAcid.BySingleLetter.get(l2.head)
    } yield ProteinSub(p, aa1.code, aa2.code)
  }

  def proteinSub(pos: String, c1: String, c2: String): Option[ProteinSub] = {
    (for {
      p <- Try(pos.toInt)
      aa1 <- Try(AminoAcid.Code.withName(c1))
      aa2 <- Try(AminoAcid.Code.withName(c2))
    } yield ProteinSub(p, aa1, aa2)).toOption
  }

  def parse(hgvs: String): Option[ProteinVariant] = {
    hgvs match {
      case rProteinSub(c1, p, c2)       => proteinSub(p, c1, c2)
      case rProteinSubLetter(l1, p, l2) => proteinSubLetter(p, l1, l2)
      case _                            => None
    }
  }
}
