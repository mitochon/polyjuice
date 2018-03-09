package polyjuice.tracer

import polyjuice.model._

case class MutationTracer(gene: Gene) {
  import MutationTracer._

  val cdsTracer = CodingSequenceTracer(gene)
  val codonTracer = CodonTracer(gene)

  def cds(pos: Int, from: Base, to: Base, transcript: Transcript): Option[Snv] = {
    for {
      strand <- gene.get(transcript).map(_.strand)
      single <- cdsTracer.coord(pos, transcript)
      if checkMatch(single, from, strand)
    } yield VariantBuilder.build(single, to, strand)
  }

  def cds(pos: Int, from: Base, to: Base): Map[Transcript, Snv] = {
    for {
      (transcript, single) <- cdsTracer.coord(pos)
      strand <- gene.get(transcript).map(_.strand)
      if checkMatch(single, from, strand)
    } yield (transcript, VariantBuilder.build(single, to, strand))
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
    } yield to.codons.flatMap(generateVariant(triple, _, strand))

    variants.getOrElse(Set())
  }

  def aminoAcid(
    pos: Int,
    from: AminoAcid,
    to: AminoAcid): Map[Transcript, Set[VariantCoord]] = {

    for {
      (transcript, triple) <- codonTracer.coord(pos)
      strand <- gene.get(transcript).map(_.strand)
      if checkMatch(triple, from, strand)
    } yield (transcript, to.codons.flatMap(generateVariant(triple, _, strand)))
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

  def diff(from: Codon, to: Codon): (Boolean, Boolean, Boolean) = {
    (from.first != to.first, from.second != to.second, from.third != to.third)
  }

  // TODO
  def generateVariant(triple: Triple, codon: Codon, strand: Strand.Value): Option[VariantCoord] = {
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