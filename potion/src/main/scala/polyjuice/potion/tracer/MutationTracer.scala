package polyjuice.potion.tracer

import polyjuice.potion.model._

case class MutationTracer(gene: Gene) {
  import MutationTracer._

  val cdsTracer = CodingSequenceTracer(gene)
  val codonTracer = CodonTracer(gene)

  def cds(sub: CdsSub, transcript: Transcript): Option[Snv] = {
    for {
      strand <- gene.get(transcript).map(_.strand)
      single <- cdsTracer.coord(sub.pos, transcript)
      if checkMatch(single, sub.from, strand)
    } yield VariantBuilder.snv(single, sub.to, strand)
  }

  def cds(sub: CdsSub): Map[Transcript, Snv] = {
    for {
      (transcript, single) <- cdsTracer.coord(sub.pos)
      strand <- gene.get(transcript).map(_.strand)
      if checkMatch(single, sub.from, strand)
    } yield (transcript, VariantBuilder.snv(single, sub.to, strand))
  }

  def cds(cvar: CdsVariant, transcript: Transcript): Option[Snv] = {
    cvar match {
      case sub: CdsSub => cds(sub, transcript)
      case _           => None
    }
  }

  def cds(cvar: CdsVariant): Map[Transcript, Snv] = {
    cvar match {
      case sub: CdsSub => cds(sub)
      case _           => Map()
    }
  }

  def aminoAcid(sub: ProteinSub, transcript: Transcript): Set[VariantCoord] = {
    val variants = for {
      triple <- codonTracer.coord(sub.pos, transcript)
      strand <- gene.get(transcript).map(_.strand)
      from <- AminoAcid.ByCode.get(sub.from)
      to <- AminoAcid.ByCode.get(sub.to)
      if checkMatch(triple, from, strand)
    } yield to.codons.flatMap(VariantBuilder.build(triple, _, strand))

    variants.getOrElse(Set())
  }

  def aminoAcid(sub: ProteinSub): Map[Transcript, Set[VariantCoord]] = {
    for {
      (transcript, triple) <- codonTracer.coord(sub.pos)
      strand <- gene.get(transcript).map(_.strand)
      from <- AminoAcid.ByCode.get(sub.from)
      to <- AminoAcid.ByCode.get(sub.to)
      if checkMatch(triple, from, strand)
    } yield (transcript, to.codons.flatMap(VariantBuilder.build(triple, _, strand)))
  }

  def aminoAcid(pvar: ProteinVariant, transcript: Transcript): Set[VariantCoord] = {
    pvar match {
      case sub: ProteinSub => aminoAcid(sub, transcript)
      case _               => Set()
    }
  }

  def aminoAcid(pvar: ProteinVariant): Map[Transcript, Set[VariantCoord]] = {
    pvar match {
      case sub: ProteinSub => aminoAcid(sub)
      case _               => Map()
    }
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