package polyjuice.phial

import polyjuice.phial.model.HgvsEntry
import polyjuice.potion.model._
import polyjuice.potion.parser._
import polyjuice.potion.tracer._
import polyjuice.potion.vcf._

case class Api(genes: Map[GeneSymbol, Gene], ensemblBuild: String) {

  val transcripts = genes.foldLeft(Map[Transcript, GeneSymbol]())(addGeneTranscripts)

  def addGeneTranscripts(
    map: Map[Transcript, GeneSymbol],
    entry: (GeneSymbol, Gene)): Map[Transcript, GeneSymbol] = {

    def addTranscript(g: GeneSymbol)(
      m: Map[Transcript, GeneSymbol],
      e: (Transcript, EnsemblGene)): Map[Transcript, GeneSymbol] = {
      m + (e._1 -> g)
    }

    val (gs, gene) = entry
    gene.foldLeft(map)(addTranscript(gs))
  }

  def getGene(g: GeneSymbol): Option[Gene] = {
    genes.get(g)
  }

  def getTranscript(t: Transcript): Option[Gene] = {
    transcripts.get(t).flatMap(getGene)
  }

  def cdsPos(g: GeneSymbol, pos: Int): Option[Map[Transcript, Base]] = {
    genes.get(g).map(CodingSequenceTracer(_).cds(pos))
  }

  def cdsCoord(g: GeneSymbol, pos: Int): Option[Map[Transcript, Single]] = {
    genes.get(g).map(CodingSequenceTracer(_).coord(pos))
  }

  def cdsTranscriptPos(t: Transcript, pos: Int): Option[Base] = {
    for {
      g <- transcripts.get(t)
      m <- cdsPos(g, pos)
      b <- m.get(t)
    } yield b
  }

  def cdsTranscriptCoord(t: Transcript, pos: Int): Option[Single] = {
    for {
      g <- transcripts.get(t)
      m <- cdsCoord(g, pos)
      b <- m.get(t)
    } yield b
  }

  def codonPos(g: GeneSymbol, pos: Int): Option[Map[Transcript, Codon]] = {
    genes.get(g).map(CodonTracer(_).codon(pos))
  }

  def codonCoord(g: GeneSymbol, pos: Int): Option[Map[Transcript, Triple]] = {
    genes.get(g).map(CodonTracer(_).coord(pos))
  }

  def codonTranscriptPos(t: Transcript, pos: Int): Option[Codon] = {
    for {
      g <- transcripts.get(t)
      m <- codonPos(g, pos)
      c <- m.get(t)
    } yield c
  }

  def codonTranscriptCoord(t: Transcript, pos: Int): Option[Triple] = {
    for {
      g <- transcripts.get(t)
      m <- codonCoord(g, pos)
      c <- m.get(t)
    } yield c
  }

  def exonNum(g: GeneSymbol, num: Int): Option[Map[Transcript, Exon]] = {
    genes.get(g).map(ExonTracer(_).exon(num))
  }

  def exonNumTranscript(t: Transcript, num: Int): Option[Exon] = {
    for {
      g <- transcripts.get(t)
      m <- exonNum(g, num)
      e <- m.get(t)
    } yield e
  }

  def hgvsPName(hgvs: String, g: GeneSymbol): Option[Map[Transcript, Set[VariantCoord]]] = {
    for {
      p <- PNameParser.parse(hgvs)
      m <- genes.get(g).map(ProteinVariantTracer(_))
    } yield m.aminoAcid(p)
  }

  def hgvsPNameTranscript(hgvs: String, t: Transcript): Option[Set[VariantCoord]] = {
    for {
      p <- PNameParser.parse(hgvs)
      g <- transcripts.get(t)
      m <- genes.get(g).map(ProteinVariantTracer(_))
    } yield m.aminoAcid(p, t)
  }

  def hgvsCName(hgvs: String, g: GeneSymbol): Option[Map[Transcript, VariantCoord]] = {
    for {
      c <- CNameParser.parse(hgvs)
      m <- genes.get(g).map(CdsVariantTracer(_))
    } yield m.cds(c)
  }

  def hgvsCNameTranscript(hgvs: String, t: Transcript): Option[VariantCoord] = {
    for {
      c <- CNameParser.parse(hgvs)
      g <- transcripts.get(t)
      m <- genes.get(g).map(CdsVariantTracer(_))
      s <- m.cds(c, t)
    } yield s
  }

  def hgvsPName(hgvs: String): Option[ProteinVariant] = {
    PNameParser.parse(hgvs)
  }

  def hgvsCName(hgvs: String): Option[CdsVariant] = {
    CNameParser.parse(hgvs)
  }

  def hgvs2vcf(entries: Seq[HgvsEntry]): String = {
    val builder = VcfBuilder(entries)
    val (trNoMatch, trLines) = VcfBuilder.partitionOutcome(builder.buildTranscriptCoords(this))
    val (geneNoMatch, geneLines) = VcfBuilder.partitionOutcome(builder.buildGeneCoords(this))
    val unmatchedEntries = trNoMatch ++ geneNoMatch
    val allMatches = (trLines ++ geneLines).flatMap(VcfBuilder.addEntryAsInfoKey)

    VcfLine.printVcf(allMatches, builder.buildMetaKeys(this)).mkString("\n")
  }
}