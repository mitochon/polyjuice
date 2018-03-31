package polyjuice.potion.io

import java.io.IOException
import java.nio.file.Path

import polyjuice.potion.model._

object EnsemblGeneReader {

  case class GeneParseError(
    fastaParseErrors: Seq[Throwable],
    gff3ParseErrors: Seq[Throwable],
    exonParseErrors: Seq[Throwable],
    utrParseErrors: Seq[Throwable]) extends Exception {

    def isEmpty: Boolean = {
      fastaParseErrors.isEmpty && gff3ParseErrors.isEmpty &&
        exonParseErrors.isEmpty && utrParseErrors.isEmpty
    }
  }

  def removeTranscriptBuild(t: String): String = {
    t.takeWhile(_ != '.')
  }

  def getTranscripts(xs: Seq[EnsemblFastaHeaderRecord]): Set[String] = {
    xs.map(_.transcript).map(removeTranscriptBuild).toSet
  }

  def addExon(gene: Gene, exon: Exon): Gene = {
    gene.get(exon.transcript) match {
      case Some(g) => gene + (exon.transcript -> g.copy(exons = g.exons :+ exon))
      case None    => gene
    }
  }

  def addUTR(gene: Gene, utr: UTR): Gene = {
    def copyUTR(g: EnsemblGene) = utr match {
      case u5: UTR5 => g.copy(utr5 = g.utr5 :+ u5)
      case u3: UTR3 => g.copy(utr3 = g.utr3 :+ u3)
      case _        => g
    }

    gene.get(utr.transcript) match {
      case Some(g) => gene + (utr.transcript -> copyUTR(g))
      case None    => gene
    }
  }

  @throws[IOException]
  def getGene(
    geneSymbol: GeneSymbol,
    cdsFastaPath: Path,
    gff3Path: Path): Either[GeneParseError, Gene] = {

    get(Set(geneSymbol), cdsFastaPath, gff3Path).map(_.get(geneSymbol).getOrElse(emptyGene))
  }

  @throws[IOException]
  def get(
    genes: Set[GeneSymbol],
    cdsFastaPath: Path,
    gff3Path: Path): Either[GeneParseError, Map[GeneSymbol, Gene]] = {

    def readFasta: Seq[Line[EnsemblFastaHeaderRecord]] = {
      EnsemblFastaReader.readHeaders(cdsFastaPath, r => genes.contains(r.geneSymbol), _.toList)
    }

    def readFastaContents(transcripts: Set[String]): Map[Transcript, String] = {
      EnsemblFastaReader.readContents(cdsFastaPath, transcripts)
    }

    def readGff3(transcripts: Set[String]): Seq[Line[EnsemblGff3Record]] = {
      EnsemblGff3Reader.readGff3(gff3Path, EnsemblGff3Reader
        .transcriptFilter(transcripts), _.toList)
    }

    def addFastaHeader(map: Map[Transcript, String])(
      gene: Gene,
      fastaHeader: EnsemblFastaHeaderRecord): Gene = {

      map.get(fastaHeader.transcript) match {
        case None => gene
        case Some(bases) => gene +
          (removeTranscriptBuild(fastaHeader.transcript) ->
            EnsemblGene(
              fastaHeader.geneSymbol,
              fastaHeader.transcript,
              fastaHeader.contig,
              fastaHeader.start,
              fastaHeader.stop,
              fastaHeader.strand,
              IndexedSeq(),
              IndexedSeq(),
              IndexedSeq(),
              bases))
      }
    }

    val fastaLines = readFasta
    val fastaTranscripts = fastaLines.flatMap(_.right.toOption)
    val fastaContents = readFastaContents(fastaTranscripts.map(_.transcript).toSet)

    val gff3Lines = readGff3(getTranscripts(fastaTranscripts))
    val gff3Records = gff3Lines.filter(_.isRight)

    val utrs = gff3Records.filter(lineFilter(_, EnsemblGff3Reader.isUTRRecord))
      .map(EnsemblGff3Reader.readUTR)

    val exons = gff3Records.filter(lineFilter(_, EnsemblGff3Reader.isExonRecord))
      .map(EnsemblGff3Reader.readExon)

    val parseError = GeneParseError(
      fastaLines.flatMap(_.left.toOption),
      gff3Lines.flatMap(_.left.toOption),
      exons.flatMap(_.left.toOption),
      utrs.flatMap(_.left.toOption))

    if (!parseError.isEmpty) Left(parseError)
    else {
      val withTranscripts = fastaTranscripts.foldLeft(emptyGene)(addFastaHeader(fastaContents))
      val withExons = exons.flatMap(_.right.toOption).foldLeft(withTranscripts)(addExon)
      val withUTRs = utrs.flatMap(_.right.toOption).foldLeft(withExons)(addUTR)
      Right(withUTRs.groupBy(_._2.geneSymbol))
    }
  }
}