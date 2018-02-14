package polyjuice.io

import java.io.InputStream
import java.nio.file.{ Files, Path }
import java.util.zip.GZIPInputStream

import scala.io.Source
import scala.util.Try

import polyjuice.model.{ EnsemblGff3Record, Exon }

object EnsemblExonReader {

  val ExonFeature = "exon"
  val CommentPrefix = "#"

  def transcriptFilter(transcript: String): EnsemblGff3Record => Boolean = {
    (r: EnsemblGff3Record) => r.getParentTranscript == Some(transcript)
  }

  def readGff3[A](
    gff3Path: Path,
    filter: EnsemblGff3Record => Boolean,
    fn: Iterator[Either[Throwable, Exon]] => A): A = {

    var stream: InputStream = null
    try {
      stream = new GZIPInputStream(Files.newInputStream(gff3Path))
      fn(readGff3(stream, filter))
    } finally {
      if (stream != null) stream.close
    }
  }

  def readGff3(
    stream: InputStream,
    filter: EnsemblGff3Record => Boolean): Iterator[Either[Throwable, Exon]] = {

    val reader = Source.fromInputStream(stream)

    def checkRecord(record: EnsemblGff3Record): Boolean = {
      record.feature.equals(ExonFeature) && filter(record)
    }

    reader.getLines()
      .filterNot(_.startsWith(CommentPrefix))
      .map(line => Try(EnsemblGff3Record(line)).toEither)
      .filter(e => e.isLeft || e.isRight && e.forall(checkRecord))
      .map(_.map(r => Try(Exon(r)).toEither))
      .map(_.joinRight)
  }
}