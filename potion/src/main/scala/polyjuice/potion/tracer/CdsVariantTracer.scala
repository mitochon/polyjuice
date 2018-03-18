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

  def cds(ins: CdsIns, transcript: Transcript): Option[Ins] = {
    for {
      strand <- gene.get(transcript).map(_.strand)
      start <- cdsTracer.coord(ins.start, transcript)
      end <- cdsTracer.coord(ins.end, transcript)
    } yield VariantBuilder.ins(start, end, ins.bases, strand)
  }

  def cds(ins: CdsIns): Map[Transcript, Ins] = {
    for {
      (transcript, start) <- cdsTracer.coord(ins.start)
      end <- cdsTracer.coord(ins.end, transcript)
      strand <- gene.get(transcript).map(_.strand)
    } yield (transcript, VariantBuilder.ins(start, end, ins.bases, strand))
  }

  def cds(cvar: CdsVariant, transcript: Transcript): Option[VariantCoord] = {
    cvar match {
      case sub: CdsSub => cds(sub, transcript)
      case ins: CdsIns => cds(ins, transcript)
      case _           => None
    }
  }

  def cds(cvar: CdsVariant): Map[Transcript, VariantCoord] = {
    cvar match {
      case sub: CdsSub => cds(sub)
      case ins: CdsIns => cds(ins)
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