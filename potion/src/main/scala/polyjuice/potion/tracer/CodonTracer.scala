package polyjuice.tracer

import scala.util.Try

import polyjuice.model._

case class CodonTracer(gene: Gene) {
  import CodonTracer._

  def codon(pos: Int, transcript: Transcript): Option[Codon] = {
    gene.get(transcript).flatMap(lookup(_, pos))
  }

  def codon(pos: Int): Map[Transcript, Codon] = {
    for {
      (transcipt, g) <- gene
      codon <- lookup(g, pos)
    } yield (transcipt, codon)
  }

  def aminoAcid(pos: Int, transcript: Transcript): Option[AminoAcid] = {
    codon(pos, transcript).flatMap(AminoAcid.ByCodon.get)
  }

  def aminoAcid(pos: Int): Map[Transcript, AminoAcid] = {
    for {
      (transcript, c) <- codon(pos)
      aminoAcid <- AminoAcid.ByCodon.get(c)
    } yield (transcript, aminoAcid)
  }

  def coord(pos: Int, transcript: Transcript): Option[Triple] = {
    for {
      c <- codon(pos, transcript)
      g <- gene.get(transcript)
      o <- seek(g, pos)
    } yield o.triple(c)
  }

  def coord(pos: Int): Map[Transcript, Triple] = {
    for {
      (transcript, _) <- codon(pos)
      t <- coord(pos, transcript)
    } yield (transcript, t)
  }
}

object CodonTracer {

  def seek(g: EnsemblGene, pos: Int): Option[Offset] = {
    CodingSequenceTracer.seek(g, (pos * 3) - 2)
  }

  def lookup(g: EnsemblGene, pos: Int): Option[Codon] = {
    val bases = g.codingSequence.drop((pos - 1) * 3).take(3)
    if (bases.isEmpty) None else Try(Codon(bases)).toOption
  }
}