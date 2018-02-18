package polyjuice.io

import java.io.InputStream
import java.nio.file.{ Files, Path }
import java.util.zip.GZIPInputStream

import scala.io.Source
import scala.util.Try

import polyjuice.model.{ EnsemblGff3Record, Exon, UTR }

object EnsemblExonReader {

  val CommentPrefix = "#"
  val ExonFeature = "exon"
  val FivePrimeUTRFeature = "five_prime_UTR"
  val ThreePrimeUTRFeature = "three_prime_UTR"

  def transcriptFilter(transcript: String): EnsemblGff3Record => Boolean = {
    (r: EnsemblGff3Record) => r.getParentTranscript == Some(transcript)
  }

  def isExonRecord(record: EnsemblGff3Record): Boolean = {
    record.feature.equals(ExonFeature)
  }

  def isUTRRecord(record: EnsemblGff3Record): Boolean = {
    record.feature.equals(FivePrimeUTRFeature) ||
      record.feature.equals(ThreePrimeUTRFeature)
  }

  def readExon(line: Line[EnsemblGff3Record]): Line[Exon] = {
    line.map(r => Try(Exon(r)).toEither).joinRight
  }

  def readUTR(line: Line[EnsemblGff3Record]): Line[UTR] = {
    line.map(r => Try(UTR(r)).toEither).joinRight
  }

  def readGff3[A](
    gff3Path: Path,
    filter: EnsemblGff3Record => Boolean,
    fn: Iterator[Line[EnsemblGff3Record]] => A): A = {

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
    filter: EnsemblGff3Record => Boolean): Iterator[Line[EnsemblGff3Record]] = {

    Source.fromInputStream(stream)
      .getLines()
      .filterNot(_.startsWith(CommentPrefix))
      .map(line => Try(EnsemblGff3Record(line)).toEither)
      .filter(lineFilter(_, filter))
  }
}