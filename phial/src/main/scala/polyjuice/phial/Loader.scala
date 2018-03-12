package polyjuice.phial

import java.nio.file.{ Files, Path, Paths }

import scala.collection.JavaConverters._

import com.typesafe.scalalogging.LazyLogging

import polyjuice.potion.io.EnsemblGeneReader
import polyjuice.potion.io.EnsemblGeneReader.GeneParseError
import polyjuice.potion.model.{ Gene, GeneSymbol }

object Loader extends LazyLogging {

  def checkExists(p: Path): Boolean = {
    Files.exists(p) && Files.isReadable(p)
  }

  @throws[Exception]
  def init: Map[GeneSymbol, Gene] = {
    require(checkExists(WebServerConfig.EnsemblCdsFastaPath) &&
      checkExists(WebServerConfig.EnsemblFeatureGff3Path))

    def printFailure(parserError: GeneParseError): Unit = {
      parserError.fastaParseErrors.foreach(logger.error("Error loading fasta line", _))
      parserError.gff3ParseErrors.foreach(logger.error("Error loading gff3 line", _))
      parserError.exonParseErrors.foreach(logger.error("Error loading exon data", _))
      parserError.utrParseErrors.foreach(logger.error("Error loading utr data", _))
    }

    def printSuccess(map: Map[GeneSymbol, Gene]): Unit = {
      map.foreach {
        case (g, gene) => logger.info(s"Loaded gene $g with ${gene.size} transcripts")
      }
    }

    val GeneLoader = EnsemblGeneReader.get(
      WebServerConfig.GeneList.asScala.toSet,
      WebServerConfig.EnsemblCdsFastaPath,
      WebServerConfig.EnsemblFeatureGff3Path)

    GeneLoader match {
      case Left(parserError) => printFailure(parserError)
      case Right(geneMap)    => printSuccess(geneMap)
    }

    require(GeneLoader.isRight)
    GeneLoader.right.get
  }
}