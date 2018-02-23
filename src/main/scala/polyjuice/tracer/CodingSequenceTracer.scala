package polyjuice.tracer

import polyjuice.model._
import scala.util.Try

case class CodingSequenceTracer(gene: Gene) {
  import CodingSequenceTracer._

  def cds(pos: Int, transcript: Transcript): Option[Base] = {
    gene.get(transcript).flatMap(lookup(_, pos))
  }

  def cds(pos: Int): Map[Transcript, Base] = {
    for {
      (transcipt, g) <- gene
      base <- lookup(g, pos)
    } yield (transcipt, base)
  }
}

object CodingSequenceTracer {

  def lookup(g: EnsemblGene, pos: Int): Option[Base] = {
    val base = g.codingSequence.drop(pos - 1).take(1)
    if (base.isEmpty) None else Try(Base(base(0))).toOption
  }
}