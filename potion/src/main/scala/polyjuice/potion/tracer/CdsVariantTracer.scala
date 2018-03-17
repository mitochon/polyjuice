package polyjuice.potion.tracer

import polyjuice.potion.model._

case class CdsVariantTracer(gene: Gene) {
  import CdsVariantTracer._

  val cdsTracer = CodingSequenceTracer(gene)

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
}

object CdsVariantTracer {

  def checkMatch(s: Single, b: Base, strand: Strand.Value): Boolean = {
    strand match {
      case Strand.Plus  => s.base == b
      case Strand.Minus => s.base.complement == b
    }
  }
}