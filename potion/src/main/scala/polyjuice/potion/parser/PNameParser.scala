package polyjuice.potion.parser

import scala.util.Try

import polyjuice.potion.model._

object PNameParser {

  // e.g. p.T790M
  val rProteinLetterSub = """(?:p\.)?([A-Y])([\d]+)([A-Y])""".r

  // e.g. p.Thr790Met
  val rProteinSub = """(?:p\.)?([A-V][a-z][a-z])([\d]+)([A-V][a-z][a-z])""".r

  def proteinSub(pos: String, aa1: String, aa2: String): Option[ProteinSub] = {
    for {
      p <- Try(pos.toInt).toOption
      aa1 <- AminoAcid.BySingleLetter.get(aa1.head)
      aa2 <- AminoAcid.BySingleLetter.get(aa2.head)
    } yield ProteinSub(p, aa1.code, aa2.code)
  }

  def parse(hgvs: String): Option[ProteinVariant] = {
    hgvs match {
      case rProteinSub(aa1, p, aa2) => proteinSub(p, aa1, aa2)
      case _                        => None
    }
  }
}
