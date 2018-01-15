package polyjuice.io

import java.io.InputStream
import java.nio.file.{ Files, Path }
import java.util.zip.GZIPInputStream

import scala.io.Source
import scala.util.{ Failure, Success, Try }

import com.typesafe.scalalogging.LazyLogging

import polyjuice.model.{ EnsemblGff3Record, Exon }

object EnsemblExonReader extends LazyLogging {

  val ExonFeature = "exon"
  val CommentPrefix = "#"

  // TODO replace logging with Either
  def logErr(e: Throwable, ref: Any): Unit = {
    logger.error(s"$e while processing $ref")
  }

  def readGFF3(gff3Path: Path, transcript: String): Seq[Exon] = {
    var stream: InputStream = null
    try {
      stream = new GZIPInputStream(Files.newInputStream(gff3Path))
      readGFF3(stream, transcript)
    } finally {
      if (stream != null) stream.close
    }
  }

  def readGFF3(stream: InputStream, transcript: String): Seq[Exon] = {
    val transcriptValue = Some(transcript)
    val reader = Source.fromInputStream(stream)

    reader.getLines()
      .filterNot {
        line => line.startsWith(CommentPrefix)
      }
      .flatMap {
        line =>
          Try(EnsemblGff3Record(line)) match {
            case Success(record) => Some(record)
            case Failure(e)      => logErr(e, line); None
          }
      }
      .filter {
        record =>
          record.feature.equals(ExonFeature) &&
            record.getParentTranscript == transcriptValue
      }
      .flatMap {
        record =>
          Try(Exon(record)) match {
            case Success(exon) => Some(exon)
            case Failure(e)    => logErr(e, record); None
          }
      }
      .toList
  }
}