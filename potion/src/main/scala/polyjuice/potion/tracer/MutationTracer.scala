package polyjuice.potion.tracer

import polyjuice.potion.model._

case class MutationTracer(gene: Gene) {
  import MutationTracer._

  val cdsTracer = CodingSequenceTracer(gene)
  val codonTracer = CodonTracer(gene)

  def cds(pos: Int, from: Base, to: Base, transcript: Transcript): Option[Snv] = {
    for {
      strand <- gene.get(transcript).map(_.strand)
      single <- cdsTracer.coord(pos, transcript)
      if checkMatch(single, from, strand)
    } yield VariantBuilder.snv(single, to, strand)
  }

  def cds(pos: Int, from: Base, to: Base): Map[Transcript, Snv] = {
    for {
      (transcript, single) <- cdsTracer.coord(pos)
      strand <- gene.get(transcript).map(_.strand)
      if checkMatch(single, from, strand)
    } yield (transcript, VariantBuilder.snv(single, to, strand))
  }

  def aminoAcid(
    pos: Int,
    from: Char,
    to: Char,
    transcript: Transcript): Set[VariantCoord] = {
    (for {
      f <- AminoAcid.BySingleLetter.get(from)
      t <- AminoAcid.BySingleLetter.get(to)
    } yield aminoAcid(pos, f, t, transcript)).getOrElse(Set())
  }

  def aminoAcid(
    pos: Int,
    from: AminoAcid.Code.Value,
    to: AminoAcid.Code.Value,
    transcript: Transcript): Set[VariantCoord] = {
    (for {
      f <- AminoAcid.All.get(from)
      t <- AminoAcid.All.get(to)
    } yield aminoAcid(pos, f, t, transcript)).getOrElse(Set())
  }

  def aminoAcid(
    pos: Int,
    from: AminoAcid,
    to: AminoAcid,
    transcript: Transcript): Set[VariantCoord] = {

    val variants = for {
      triple <- codonTracer.coord(pos, transcript)
      strand <- gene.get(transcript).map(_.strand)
      if checkMatch(triple, from, strand)
    } yield to.codons.flatMap(VariantBuilder.build(triple, _, strand))

    variants.getOrElse(Set())
  }

  def aminoAcid(
    pos: Int,
    from: Char,
    to: Char): Map[Transcript, Set[VariantCoord]] = {
    (for {
      f <- AminoAcid.BySingleLetter.get(from)
      t <- AminoAcid.BySingleLetter.get(to)
    } yield aminoAcid(pos, f, t)).getOrElse(Map())
  }

  def aminoAcid(
    pos: Int,
    from: AminoAcid.Code.Value,
    to: AminoAcid.Code.Value): Map[Transcript, Set[VariantCoord]] = {
    (for {
      f <- AminoAcid.All.get(from)
      t <- AminoAcid.All.get(to)
    } yield aminoAcid(pos, f, t)).getOrElse(Map())
  }

  def aminoAcid(
    pos: Int,
    from: AminoAcid,
    to: AminoAcid): Map[Transcript, Set[VariantCoord]] = {

    for {
      (transcript, triple) <- codonTracer.coord(pos)
      strand <- gene.get(transcript).map(_.strand)
      if checkMatch(triple, from, strand)
    } yield (transcript, to.codons.flatMap(VariantBuilder.build(triple, _, strand)))
  }
}

object MutationTracer {

  def checkMatch(s: Single, b: Base, strand: Strand.Value): Boolean = {
    strand match {
      case Strand.Plus  => s.base == b
      case Strand.Minus => s.base.complement == b
    }
  }

  def checkMatch(t: Triple, aa: AminoAcid, strand: Strand.Value): Boolean = {
    strand match {
      case Strand.Plus  => AminoAcid.ByCodon.get(t.bases).exists(_ == aa)
      case Strand.Minus => AminoAcid.ByCodon.get(t.bases.flip).exists(_ == aa)
    }
  }
}