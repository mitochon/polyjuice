package polyjuice.io

import java.io.IOException
import java.nio.file.Path

import scala.io.{ BufferedSource, Source }
import scala.util.Try

import htsjdk.samtools.reference.{ IndexedFastaSequenceFile, ReferenceSequence }
import polyjuice.model.EnsemblFastaHeaderRecord

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
  def readFasta(fastaPath: Path, transcript: String): ReferenceSequence = {
    var fa: IndexedFastaSequenceFile = null
    try {
      fa = new IndexedFastaSequenceFile(fastaPath)
      fa.getSequence(transcript)
    } finally {
      if (fa != null) fa.close()
    }
  }
}