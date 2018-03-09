package polyjuice.tracer

import polyjuice.model._

case class MutationTracer(gene: Gene) {
  import MutationTracer._

  val cdsTracer = CodingSequenceTracer(gene)
  val codonTracer = CodonTracer(gene)

  def cds(pos: Int, from: Base, to: Base, transcript: Transcript): Option[Snv] = {
    for {
      single <- cdsTracer.coord(pos, transcript)
      if (single.base == from)
    } yield Snv(single.contig, single.pos, from, to)
  }

  def cds(pos: Int, from: Base, to: Base): Map[Transcript, Snv] = {
    for {
      (transcript, single) <- cdsTracer.coord(pos)
      if (single.base == from)
    } yield (transcript, Snv(single.contig, single.pos, from, to))
  }

  def aminoAcid(
    pos: Int,
    from: AminoAcid,
    to: AminoAcid,
    transcript: Transcript): Set[VariantCoord] = {

    val variants = for {
      t <- codonTracer.coord(pos, transcript)
      a <- AminoAcid.ByCodon.get(t.bases)
      if (a == from)
    } yield to.codons.flatMap(generateVariant(t, _))

    variants.getOrElse(Set())
  }

  def aminoAcid(
    pos: Int,
    from: AminoAcid,
    to: AminoAcid): Map[Transcript, Set[VariantCoord]] = {

    for {
      (transcript, t) <- codonTracer.coord(pos)
      a <- AminoAcid.ByCodon.get(t.bases)
      if (a == from)
    } yield (transcript, to.codons.flatMap(generateVariant(t, _)))
  }
}

object MutationTracer {

  def diff(from: Codon, to: Codon): (Boolean, Boolean, Boolean) = {
    (from.first != to.first, from.second != to.second, from.third != to.third)
  }

  def generateVariant(triple: Triple, codon: Codon): Option[VariantCoord] = {
    diff(triple.bases, codon) match {
      case (false, false, false) => None
      case (true, false, false)  => Some(Snv(triple.contig, triple.pos, triple.bases.first, codon.first))
      case (false, true, false)  => Some(Snv(triple.contig, triple.pos + 1, triple.bases.second, codon.second))
      case (false, false, true)  => Some(Snv(triple.contig, triple.pos + 2, triple.bases.third, codon.third))
      case (true, true, false)   => Some(Mnv(triple.contig, triple.pos, triple.bases.toSeq.take(2), codon.toSeq.take(2)))
      case (false, true, true)   => Some(Mnv(triple.contig, triple.pos + 1, triple.bases.toSeq.drop(1), codon.toSeq.drop(1)))
      case (true, false, true)   => Some(Mnv(triple.contig, triple.pos, triple.bases.toSeq, codon.toSeq))
      case (true, true, true)    => Some(Mnv(triple.contig, triple.pos, triple.bases.toSeq, codon.toSeq))
    }
  }
}