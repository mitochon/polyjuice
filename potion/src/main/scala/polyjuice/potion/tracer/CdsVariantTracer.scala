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

  def cds(del: CdsDel, transcript: Transcript): Option[Del] = {
    val end = del.end.getOrElse(del.start)
    val windowSize = 1 + end - del.start

    for {
      g <- gene.get(transcript)
      leftFlank <- cdsTracer.coord(del.start - 1, transcript)
      rightFlank <- cdsTracer.coord(end + 1, transcript)
      bases <- CodingSequenceTracer.lookup(g, del.start, windowSize)
      if (del.bases.isEmpty || del.bases.contains(bases))
    } yield VariantBuilder.del(leftFlank, rightFlank, bases, g.strand)
  }

  def cds(del: CdsDel): Map[Transcript, Del] = {
    val end = del.end.getOrElse(del.start)
    val windowSize = 1 + end - del.start

    for {
      (transcript, leftFlank) <- cdsTracer.coord(del.start - 1)
      rightFlank <- cdsTracer.coord(end + 1, transcript)
      g <- gene.get(transcript)
      bases <- CodingSequenceTracer.lookup(g, del.start, windowSize)
      if (del.bases.isEmpty || del.bases.contains(bases))
    } yield (transcript, VariantBuilder.del(leftFlank, rightFlank, bases, g.strand))
  }

  def cds(cvar: CdsVariant, transcript: Transcript): Option[VariantCoord] = {
    cvar match {
      case sub: CdsSub => cds(sub, transcript)
      case ins: CdsIns => cds(ins, transcript)
      case del: CdsDel => cds(del, transcript)
      case _           => None
    }
  }

  def cds(cvar: CdsVariant): Map[Transcript, VariantCoord] = {
    cvar match {
      case sub: CdsSub => cds(sub)
      case ins: CdsIns => cds(ins)
      case del: CdsDel => cds(del)
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