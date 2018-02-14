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

  def readGff3[A](gff3Path: Path, transcript: String, fn: Iterator[Either[Throwable, Exon]] => A): A = {
    var stream: InputStream = null
    try {
      stream = new GZIPInputStream(Files.newInputStream(gff3Path))
      fn(readGff3(stream, transcript))
    } finally {
      if (stream != null) stream.close
    }
  }

  def readGff3(stream: InputStream, transcript: String): Iterator[Either[Throwable, Exon]] = {

    val transcriptValue = Some(transcript)
    val reader = Source.fromInputStream(stream)

    def matchesTranscript(record: EnsemblGff3Record): Boolean = {
      record.feature.equals(ExonFeature) && record.getParentTranscript == transcriptValue
    }

    reader.getLines()
      .filterNot(_.startsWith(CommentPrefix))
      .map(line => Try(EnsemblGff3Record(line)).toEither)
      .filter(e => e.isLeft || e.isRight && e.exists(matchesTranscript))
      .map(_.map(r => Try(Exon(r)).toEither))
      .map(_.joinRight)
  }
}