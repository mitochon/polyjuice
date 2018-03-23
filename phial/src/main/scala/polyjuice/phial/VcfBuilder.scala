package polyjuice.phial

import polyjuice.phial.model.HgvsEntry
import polyjuice.potion.model._
import polyjuice.potion.vcf._

case class VcfBuilder(entries: Seq[HgvsEntry]) {

  import VcfBuilder._

  val (pnameEntries, cnameEntries, badEntries) = HgvsEntry.partitionByHgvsType(entries)
  val (pnameGeneEntries, pnameTranscriptEntries, badPNames) = HgvsEntry.partitionByGeneOrTranscript(pnameEntries)
  val (cnameGeneEntries, cnameTranscriptEntries, badCNames) = HgvsEntry.partitionByGeneOrTranscript(cnameEntries)

  val errors = badEntries ++ badPNames ++ badCNames

  def buildGeneCoords(api: Api) = {
    val getVariant = (e: HgvsEntry) => e.gene.flatMap(api.hgvsCName(e.hgvs, _))
    val getVariantSet = (e: HgvsEntry) => e.gene.flatMap(api.hgvsPName(e.hgvs, _))

    entriesWithGene(cnameGeneEntries.map(e => (e, getVariant(e)))) ++
      entriesWithGeneSet(pnameGeneEntries.map(e => (e, getVariantSet(e))))
  }

  def buildTranscriptCoords(api: Api) = {
    val getVariant = (e: HgvsEntry) => e.transcript.flatMap(api.hgvsCNameTranscript(e.hgvs, _))
    val getVariantSet = (e: HgvsEntry) => e.transcript.flatMap(api.hgvsPNameTranscript(e.hgvs, _))

    entriesWithTranscript(cnameTranscriptEntries.map(e => (e, getVariant(e)))) ++
      entriesWithTranscriptSet(pnameTranscriptEntries.map(e => (e, getVariantSet(e))))
  }
}

object VcfBuilder {

  type HgvsVcfLine = (HgvsEntry, Seq[VcfLine])
  type HgvsVcfOutcome = Either[HgvsEntry, HgvsVcfLine]

  def partitionOutcome(outcomes: Seq[HgvsVcfOutcome]): (Seq[HgvsEntry], Seq[HgvsVcfLine]) = {
    (outcomes.flatMap(_.left.toOption), outcomes.flatMap(_.right.toOption))
  }

  def addEntryAsInfoKey(line: HgvsVcfLine): Seq[VcfLine] = {
    val (e, xs) = line
    val key = InfoKey("HGVS", Count(1), DataType.StringType, "HGVS string")
    val value = e.transcript.orElse(e.gene).getOrElse("") + s"_${e.hgvs.stripPrefix("p.").stripPrefix("c.")}"
    xs.map(x => x.copy(info = x.info + (key -> value)))
  }

  def entriesWithTranscript(xs: Seq[(HgvsEntry, Option[VariantCoord])]): Seq[HgvsVcfOutcome] = {
    entriesWithTranscriptSet(xs.map {
      case (e, opt) => (e, opt.map(Set(_)))
    })
  }

  def entriesWithTranscriptSet(xs: Seq[(HgvsEntry, Option[Set[VariantCoord]])]): Seq[HgvsVcfOutcome] = {
    def buildFromSet(e: HgvsEntry, s: Set[VariantCoord]) = {
      e.transcript.map(toVcfLine(_, s)).getOrElse(Seq())
    }
    xs.flatMap {
      case (e, Some(s)) if (s.nonEmpty) => Seq(Right((e, buildFromSet(e, s))))
      case (e, _)                       => Seq(Left(e))
    }
  }

  def entriesWithGene(xs: Seq[(HgvsEntry, Option[Map[Transcript, VariantCoord]])]): Seq[HgvsVcfOutcome] = {
    entriesWithGeneSet(xs.map {
      case (e, opt) => (e, opt.map(_.mapValues(Set(_))))
    })
  }

  def entriesWithGeneSet(xs: Seq[(HgvsEntry, Option[Map[Transcript, Set[VariantCoord]]])]): Seq[HgvsVcfOutcome] = {
    def buildFromMap(map: Map[Transcript, Set[VariantCoord]]) = {
      map.flatMap {
        case (k, v) => toVcfLine(k, v)
      }
    }
    xs.flatMap {
      case (e, Some(m)) if (m.nonEmpty) => Seq(Right((e, buildFromMap(m).toSeq)))
      case (e, _)                       => Seq(Left(e))
    }
  }

  def toVcfLine(transcript: Transcript, set: Set[VariantCoord]): Seq[VcfLine] = {
    set.toSeq.map(VcfLine(_, Some(transcript)))
  }

  def toVcfLine(map: Map[Transcript, VariantCoord]): Seq[VcfLine] = {
    map.toSeq.map {
      case (transcript, variant) => VcfLine(variant, Some(transcript))
    }
  }
}