package polyjuice.potion.io

import java.io.IOException
import java.nio.file.Path

import scala.io.{ BufferedSource, Source }
import scala.util.Try

import htsjdk.samtools.reference.{ IndexedFastaSequenceFile, ReferenceSequence }
import polyjuice.potion.model.{ EnsemblFastaHeaderRecord, Transcript }

object EnsemblFastaReader {

  @throws[IOException]
  def readHeaders[A](
    fastaPath: Path,
    filter: EnsemblFastaHeaderRecord => Boolean,
    fn: Iterator[Line[EnsemblFastaHeaderRecord]] => A): A = {

    var fa: BufferedSource = null
    try {
      fa = Source.fromFile(fastaPath.toFile())
      fn(fa.getLines()
        .filter(_.startsWith(EnsemblFastaHeaderRecord.Prefix))
        .map(line => Try(EnsemblFastaHeaderRecord(line)).toEither)
        .filter(lineFilter(_, filter)))

    } finally {
      if (fa != null) fa.close()
    }
  }

  @throws[IOException]
  def readFasta(fastaPath: Path, transcript: Transcript): ReferenceSequence = {
    var fa: IndexedFastaSequenceFile = null
    try {
      fa = new IndexedFastaSequenceFile(fastaPath)
      fa.getSequence(transcript)
    } finally {
      if (fa != null) fa.close()
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

    var fa: BufferedSource = null
    try {
      fa = Source.fromFile(fastaPath.toFile())
      val lines = fa.getLines()

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
      if (fa != null) fa.close()
    }
  }
}