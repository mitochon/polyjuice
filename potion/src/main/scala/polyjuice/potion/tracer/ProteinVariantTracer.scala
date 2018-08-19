package polyjuice.potion.tracer

import polyjuice.potion.model._

case class ProteinVariantTracer(gene: Gene) {
  import ProteinVariantTracer._

  val codonTracer = CodonTracer(gene)

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

  def aminoAcid(fs: ProteinFrameshift, transcript: Transcript): Set[VariantCoord] = {
    val variants = for {
      triple <- codonTracer.coord(fs.pos, transcript)
      strand <- gene.get(transcript).map(_.strand)
      from <- AminoAcid.ByCode.get(fs.from)
      if checkMatch(triple, from, strand)
    } yield Set(VariantBuilder.frameshift(triple, strand))

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

  def aminoAcid(fs: ProteinFrameshift): Map[Transcript, Set[VariantCoord]] = {
    for {
      (transcript, triple) <- codonTracer.coord(fs.pos)
      strand <- gene.get(transcript).map(_.strand)
      from <- AminoAcid.ByCode.get(fs.from)
      if checkMatch(triple, from, strand)
    } yield (transcript, Set(VariantBuilder.frameshift(triple, strand)))
  }

  def aminoAcid(pvar: ProteinVariant, transcript: Transcript): Set[VariantCoord] = {
    pvar match {
      case sub: ProteinSub       => aminoAcid(sub, transcript)
      case fs: ProteinFrameshift => aminoAcid(fs, transcript)
      case _                     => Set()
    }
  }

  def aminoAcid(pvar: ProteinVariant): Map[Transcript, Set[VariantCoord]] = {
    pvar match {
      case sub: ProteinSub       => aminoAcid(sub)
      case fs: ProteinFrameshift => aminoAcid(fs)
      case _                     => Map()
    }
  }
}

object ProteinVariantTracer {

  def checkMatch(t: Triple, aa: AminoAcid, strand: Strand.Value): Boolean = {
    strand match {
      case Strand.Plus  => AminoAcid.ByCodon.get(t.bases).exists(_ == aa)
      case Strand.Minus => AminoAcid.ByCodon.get(t.bases.flip).exists(_ == aa)
    }
  }
}