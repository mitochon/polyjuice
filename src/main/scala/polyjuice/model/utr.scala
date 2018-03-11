package polyjuice.model

sealed trait UTR {
  def transcript: Transcript
  def start: Int
  def end: Int

  val length = end - start + 1
}

case class UTR5(transcript: Transcript, start: Int, end: Int) extends UTR

case class UTR3(transcript: Transcript, start: Int, end: Int) extends UTR

object UTR {

  val FivePrime = "five_prime_UTR"
  val ThreePrime = "three_prime_UTR"

  @throws[Exception]
  def apply(record: EnsemblGff3Record): UTR = {
    record.feature match {
      case FivePrime  => UTR5(record.getParentTranscript.get, record.start, record.end)
      case ThreePrime => UTR3(record.getParentTranscript.get, record.start, record.end)
      case _          => throw new IllegalArgumentException("Bad UTR " + record.feature)
    }
  }
}