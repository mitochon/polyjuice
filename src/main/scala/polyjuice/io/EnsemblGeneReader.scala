package polyjuice.io

import java.nio.file.Path

import scala.io.{ BufferedSource, Source }
import scala.util.Try

import htsjdk.samtools.reference.{ IndexedFastaSequenceFile, ReferenceSequence }
import polyjuice.model.EnsemblFastaHeaderRecord

object EnsemblGeneReader {

  def readHeaders[A](fastaPath: Path, fn: Iterator[Either[Throwable, EnsemblFastaHeaderRecord]] => A): A = {
    var fa: BufferedSource = null
    try {
      fa = Source.fromFile(fastaPath.toFile())
      fn(fa.getLines()
        .filter(_.startsWith(EnsemblFastaHeaderRecord.Prefix))
        .map(line => Try(EnsemblFastaHeaderRecord(line)).toEither))

    } finally {
      if (fa != null) fa.close()
    }
  }

  def readFasta(fastaPath: Path, contig: String): ReferenceSequence = {
    var fa: IndexedFastaSequenceFile = null
    try {
      fa = new IndexedFastaSequenceFile(fastaPath)
      fa.getSequence(contig)
    } finally {
      if (fa != null) fa.close()
    }
  }
}