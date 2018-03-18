package polyjuice.potion.parser

import scala.util.Try

import polyjuice.potion.model._

object PNameParser {

  // e.g. p.Thr790Met
  val rProteinSub = """(?:p\.)?([A-V][a-z][a-z])([\d]+)([A-V][a-z][a-z])""".r
  // e.g. p.Ala3_Ser5del
  val rProteinDel = """(?:p\.)?([A-V][a-z][a-z])([\d]+)_([A-V][a-z][a-z])([\d]+)del""".r
  // e.g. p.Ala3_Ser5dup
  val rProteinDup = """(?:p\.)?([A-V][a-z][a-z])([\d]+)_([A-V][a-z][a-z])([\d]+)dup""".r
  // e.g. p.Ala3del
  val rProteinDelSingle = """(?:p\.)?([A-V][a-z][a-z])([\d]+)del""".r
  // e.g. p.Ala3dup
  val rProteinDupSingle = """(?:p\.)?([A-V][a-z][a-z])([\d]+)dup""".r
  // e.g. p.Lys2_Gly3insGlnSerLys
  val rProteinIns = """(?:p\.)?([A-V][a-z][a-z])([\d]+)_([A-V][a-z][a-z])([\d]+)ins(([A-V][a-z][a-z])+)""".r
  // e.g. p.Cys28_Lys29delinsTrp
  val rProteinDelIns = """(?:p\.)?([A-V][a-z][a-z])([\d]+)_([A-V][a-z][a-z])([\d]+)delins(([A-V][a-z][a-z])+)""".r
  // e.g. p.Cys28delinsTrpVal
  val rProteinDelInsSingle = """(?:p\.)?([A-V][a-z][a-z])([\d]+)delins(([A-V][a-z][a-z])+)""".r

  // e.g. p.T790M
  val rProteinSubLetter = """(?:p\.)?([A-Y\*])([\d]+)([A-Y\*])""".r
  // e.g. p.A3_S5del
  val rProteinDelLetter = """(?:p\.)?([A-Y\*])([\d]+)_([A-Y\*])([\d]+)del""".r
  // e.g. p.A3_S5dup
  val rProteinDupLetter = """(?:p\.)?([A-Y\*])([\d]+)_([A-Y\*])([\d]+)dup""".r
  // e.g. p.A3del
  val rProteinDelSingleLetter = """(?:p\.)?([A-Y\*])([\d]+)del""".r
  // e.g. p.A3dup
  val rProteinDupSingleLetter = """(?:p\.)?([A-Y\*])([\d]+)dup""".r
  // e.g. p.K2_G3insQSK
  val rProteinInsLetter = """(?:p\.)?([A-Y\*])([\d]+)_([A-Y\*])([\d]+)ins([A-Y\*]+)""".r
  // e.g. p.C28_K29delinsW
  val rProteinDelInsLetter = """(?:p\.)?([A-Y\*])([\d]+)_([A-Y\*])([\d]+)delins([A-Y\*]+)""".r
  // e.g. p.C28delinsWV
  val rProteinDelInsSingleLetter = """(?:p\.)?([A-Y\*])([\d]+)delins([A-Y\*]+)""".r

  // conversion functions
  type AABuilder = String => AminoAcid.Code.Value
  type AASeqBuilder = String => Seq[AminoAcid.Code.Value]

  @throws[Exception]
  val aaFromCode: AABuilder = (code: String) => AminoAcid.Code.withName(code)
  @throws[Exception]
  val aaFromLetter: AABuilder = (letter: String) => AminoAcid.BySingleLetter.get(letter.head).map(_.code).get
  @throws[Exception]
  val aaSeqFromCode: AASeqBuilder = (seq: String) => seq.sliding(3, 3).map(aaFromCode).toList
  @throws[Exception]
  val aaSeqFromLetter: AASeqBuilder = (seq: String) => seq.map(AminoAcid.BySingleLetter.get).flatMap(_.map(_.code))

  def proteinSub(
    pos: String,
    str1: String,
    str2: String,
    fn: AABuilder): Option[ProteinSub] = {

    (for {
      p <- Try(pos.toInt)
      aa1 <- Try(fn(str1))
      aa2 <- Try(fn(str2))
    } yield ProteinSub(p, aa1, aa2)).toOption
  }

  def proteinDel(
    start: String,
    startStr: String,
    end: Option[String],
    endStr: Option[String],
    fn: AABuilder): Option[ProteinDel] = {

    (for {
      s <- Try(start.toInt)
      e <- Try(end.map(_.toInt))
      sAa <- Try(fn(startStr))
      eAa <- Try(endStr.map(fn))
      if (e.isEmpty || e.exists(_ > s))
    } yield ProteinDel(s, sAa, e, eAa)).toOption
  }

  def proteinDup(
    start: String,
    startStr: String,
    end: Option[String],
    endStr: Option[String],
    fn: AABuilder): Option[ProteinDup] = {

    (for {
      s <- Try(start.toInt)
      e <- Try(end.map(_.toInt))
      sAa <- Try(fn(startStr))
      eAa <- Try(endStr.map(fn))
      if (e.isEmpty || e.exists(_ > s))
    } yield ProteinDup(s, sAa, e, eAa)).toOption
  }

  def proteinIns(
    start: String,
    startStr: String,
    end: String,
    endStr: String,
    proteinSequence: String,
    fn: AABuilder,
    gn: AASeqBuilder): Option[ProteinIns] = {

    (for {
      s <- Try(start.toInt)
      e <- Try(end.toInt)
      sAa <- Try(fn(startStr))
      eAa <- Try(fn(endStr))
      seq <- Try(gn(proteinSequence))
      if (s < e)
    } yield ProteinIns(s, sAa, e, eAa, seq.toList)).toOption
  }

  def proteinDelIns(
    start: String,
    startStr: String,
    end: Option[String],
    endStr: Option[String],
    proteinSequence: String,
    fn: AABuilder,
    gn: AASeqBuilder): Option[ProteinDelIns] = {

    (for {
      s <- Try(start.toInt)
      e <- Try(end.map(_.toInt))
      sAa <- Try(fn(startStr))
      eAa <- Try(endStr.map(fn))
      seq <- Try(gn(proteinSequence))
      if (e.isEmpty || e.exists(_ > s))
    } yield ProteinDelIns(s, sAa, e, eAa, seq.toList)).toOption
  }

  def parse(hgvs: String): Option[ProteinVariant] = {
    hgvs match {
      case rProteinSub(c1, p, c2)                 => proteinSub(p, c1, c2, aaFromCode)
      case rProteinDel(sc, s, ec, e)              => proteinDel(s, sc, Some(e), Some(ec), aaFromCode)
      case rProteinDup(sc, s, ec, e)              => proteinDup(s, sc, Some(e), Some(ec), aaFromCode)
      case rProteinDelSingle(sc, s)               => proteinDel(s, sc, None, None, aaFromCode)
      case rProteinDupSingle(sc, s)               => proteinDup(s, sc, None, None, aaFromCode)
      case rProteinIns(sc, s, ec, e, ps, _)       => proteinIns(s, sc, e, ec, ps, aaFromCode, aaSeqFromCode)
      case rProteinDelIns(sc, s, ec, e, ps, _)    => proteinDelIns(s, sc, Some(e), Some(ec), ps, aaFromCode, aaSeqFromCode)
      case rProteinDelInsSingle(sc, s, ps, _)     => proteinDelIns(s, sc, None, None, ps, aaFromCode, aaSeqFromCode)
      case rProteinSubLetter(l1, p, l2)           => proteinSub(p, l1, l2, aaFromLetter)
      case rProteinDelLetter(sl, s, el, e)        => proteinDel(s, sl, Some(e), Some(el), aaFromLetter)
      case rProteinDupLetter(sl, s, el, e)        => proteinDup(s, sl, Some(e), Some(el), aaFromLetter)
      case rProteinDelSingleLetter(sl, s)         => proteinDel(s, sl, None, None, aaFromLetter)
      case rProteinDupSingleLetter(sl, s)         => proteinDup(s, sl, None, None, aaFromLetter)
      case rProteinInsLetter(sl, s, el, e, ps)    => proteinIns(s, sl, e, el, ps, aaFromLetter, aaSeqFromLetter)
      case rProteinDelInsLetter(sl, s, el, e, ps) => proteinDelIns(s, sl, Some(e), Some(el), ps, aaFromLetter, aaSeqFromLetter)
      case rProteinDelInsSingleLetter(sl, s, ps)  => proteinDelIns(s, sl, None, None, ps, aaFromLetter, aaSeqFromLetter)
      case _                                      => None
    }
  }
}
