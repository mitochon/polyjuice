package polyjuice.potion.io

import java.io.{ IOException, InputStream }
import java.nio.file.{ Files, Path }
import java.util.zip.GZIPInputStream

import scala.io.Source
import scala.util.Try

import polyjuice.potion.model.{ EnsemblFastaHeaderRecord, Transcript }

object EnsemblFastaReader {

  @throws[IOException]
  def readHeaders[A](
    fastaPath: Path,
    filter: EnsemblFastaHeaderRecord => Boolean,
    fn: Iterator[Line[EnsemblFastaHeaderRecord]] => A): A = {

    var stream: InputStream = null
    try {
      stream = new GZIPInputStream(Files.newInputStream(fastaPath))
      fn(Source.fromInputStream(stream).getLines()
        .filter(_.startsWith(EnsemblFastaHeaderRecord.Prefix))
        .map(line => Try(EnsemblFastaHeaderRecord(line)).toEither)
        .filter(lineFilter(_, filter)))

    } finally {
      if (stream != null) stream.close()
    }
  }

  @throws[IOException]
  def readContents(
    fastaPath: Path,
    transcripts: Set[Transcript]): Map[Transcript, String] = {

    // transient variables
    var collect = false
    var map = collection.mutable.Map[Transcript, String]()
    var bases = Array[String]()
    var header = Option.empty[EnsemblFastaHeaderRecord]

    def reset(line: String): Unit = {
      bases = Array[String]()
      header = Try(EnsemblFastaHeaderRecord(line)).toOption
      collect = header.exists(rec => transcripts.contains(rec.transcript))
    }

    def updateMap(): Unit = {
      header.foreach(h => if (bases.nonEmpty) map += (h.transcript -> bases.mkString))
    }

    var stream: InputStream = null
    try {
      stream = new GZIPInputStream(Files.newInputStream(fastaPath))
      val lines = Source.fromInputStream(stream).getLines()

      while (lines.hasNext) {
        val currentLine = lines.next()

        if (currentLine.startsWith(EnsemblFastaHeaderRecord.Prefix)) {
          updateMap()
          reset(currentLine)
        } else if (collect) {
          bases = bases :+ currentLine
        }
      }
      updateMap()
      map.toMap
    } finally {
      if (stream != null) stream.close()
    }
  }
}