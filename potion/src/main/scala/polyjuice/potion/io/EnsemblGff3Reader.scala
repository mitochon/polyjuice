package polyjuice.potion.io

import java.io.{ IOException, InputStream }
import java.nio.file.{ Files, Path }
import java.util.zip.GZIPInputStream

import scala.io.Source
import scala.util.Try

import polyjuice.potion.model.{ EnsemblGff3Record, Exon, Transcript, UTR }

object EnsemblGff3Reader {

  val CommentPrefix = "#"
  val ExonFeature = "exon"

  def transcriptFilter(transcript: Transcript): EnsemblGff3Record => Boolean = {
    (r: EnsemblGff3Record) => r.getParentTranscript.exists(_.equals(transcript))
  }

  def transcriptFilter(transcripts: Set[Transcript]): EnsemblGff3Record => Boolean = {
    (r: EnsemblGff3Record) => r.getParentTranscript.exists(transcripts.contains)
  }

  def isExonRecord(record: EnsemblGff3Record): Boolean = {
    record.feature.equals(ExonFeature)
  }

  def isUTRRecord(record: EnsemblGff3Record): Boolean = {
    record.feature.equals(UTR.FivePrime) ||
      record.feature.equals(UTR.ThreePrime)
  }

  def readExon(line: Line[EnsemblGff3Record]): Line[Exon] = {
    line.map(r => Try(Exon(r)).toEither).joinRight
  }

  def readUTR(line: Line[EnsemblGff3Record]): Line[UTR] = {
    line.map(r => Try(UTR(r)).toEither).joinRight
  }

  @throws[IOException]
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