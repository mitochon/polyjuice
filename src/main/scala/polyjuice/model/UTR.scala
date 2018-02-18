package polyjuice.model

case class UTR(
  utrType: UTRType.Value,
  transcript: String,
  start: Int,
  end: Int)

object UTR {

  @throws[Exception]
  def apply(record: EnsemblGff3Record): UTR = {
    UTR(
      UTRType.withName(record.feature),
      record.getParentTranscript.get,
      record.start,
      record.end)
  }
}