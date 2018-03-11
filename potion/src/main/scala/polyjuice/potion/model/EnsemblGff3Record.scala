package polyjuice.model

case class EnsemblGff3Record(
  seqId: String,
  feature: String,
  start: Int,
  end: Int,
  strand: Option[Strand.Value],
  attributes: Map[String, String]) {

  def getParentTranscript: Option[Transcript] = {
    attributes.get(EnsemblGff3Record.AttrParent)
      .map(_.stripPrefix(EnsemblGff3Record.AttrTranscriptPrefix))
  }
}

object EnsemblGff3Record {

  val Missing = "."
  val AttrName = "Name"
  val AttrParent = "Parent"
  val AttrTranscriptPrefix = "transcript:"

  @throws[Exception]
  def apply(line: String): EnsemblGff3Record = {

    def addEntry(m: Map[String, String], entry: String): Map[String, String] = {
      val kv = entry.trim.split("=")
      if (kv.size == 2) m + (kv(0) -> kv(1))
      else throw new IllegalArgumentException(s"Bad attribute $entry in $line")
    }

    def buildMap(attrs: String): Map[String, String] = {
      attrs.split(";").foldLeft(Map[String, String]())(addEntry)
    }

    def filterMissing(token: String): Option[String] = {
      Option(token).filterNot(_.equals(Missing))
    }

    val tokens = line.split("\\t")

    if (tokens.size < 9) throw new IllegalArgumentException(s"Bad gff line $line")
    else
      EnsemblGff3Record(
        tokens(0),
        tokens(2),
        tokens(3).toInt,
        tokens(4).toInt,
        filterMissing(tokens(6)).map(Strand.withName),
        buildMap(tokens(8)))
  }
}