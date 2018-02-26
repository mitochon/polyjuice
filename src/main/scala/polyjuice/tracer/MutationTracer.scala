package polyjuice.tracer

import polyjuice.model._

case class MutationTracer(gene: Gene) {

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
}