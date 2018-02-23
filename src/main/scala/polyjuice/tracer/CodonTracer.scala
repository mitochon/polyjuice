package polyjuice.tracer

import scala.util.Try

import polyjuice.model._

case class CodonTracer(gene: Gene) {
  import CodonTracer._

  def codon(pos: Int, transcript: Transcript): Option[Codon] = {
    gene.get(transcript).flatMap(lookup(_, pos))
  }

  def aminoAcid(pos: Int, transcript: Transcript): Option[AminoAcid] = {
    codon(pos, transcript).flatMap(AminoAcid.ByCodon.get)
  }

  def codon(pos: Int): Map[Transcript, Codon] = {
    for {
      (transcipt, g) <- gene
      codon <- lookup(g, pos)
    } yield (transcipt, codon)
  }

  def aminoAcid(pos: Int): Map[Transcript, AminoAcid] = {
    for {
      (transcript, c) <- codon(pos)
      aminoAcid <- AminoAcid.ByCodon.get(c)
    } yield (transcript, aminoAcid)
  }
}

object CodonTracer {

  def lookup(g: EnsemblGene, codonNumber: Int): Option[Codon] = {
    val bases = g.codingSequence.drop((codonNumber - 1) * 3).take(3)
    if (bases.isEmpty) None else Try(Codon(bases)).toOption
  }
}