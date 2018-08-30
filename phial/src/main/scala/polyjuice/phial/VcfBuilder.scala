package polyjuice.phial

import polyjuice.phial.model._
import polyjuice.potion.model._
import polyjuice.potion.vcf._

case class VcfBuilder(req: Hgvs2VcfRequest) {

  import VcfBuilder._

  val (pnameEntries, cnameEntries, badEntries) = HgvsEntry.partitionByHgvsType(req.entries)
  val (pnameGeneEntries, pnameTranscriptEntries, badPNames) = HgvsEntry.partitionByGeneOrTranscript(pnameEntries)
  val (cnameGeneEntries, cnameTranscriptEntries, badCNames) = HgvsEntry.partitionByGeneOrTranscript(cnameEntries)

  val errors = badEntries ++ badPNames ++ badCNames
  val infoKeys = req.appendInfoFields.map(VcfKeyBuilder.buildMap(_, _.buildInfoKey))
  val formatKeys = req.appendFormatFields.map(VcfKeyBuilder.buildMap(_, _.buildFormatKey))
  val maxPerEntry = req.maxVariantsPerEntry.filter(_ > 0)

  def applyEntryLimit(variants: Set[VariantCoord]): Set[VariantCoord] = {
    maxPerEntry.fold(variants)(max => variants.toSeq.sorted(VariantCoord.OrderingByBaseLength).take(max).toSet)
  }

  def buildGeneCoords(api: Api): Seq[HgvsVcfOutcome] = {
    val getVariant = (e: HgvsEntry) => e.gene.flatMap(api.hgvsCName(e.hgvs, _))
    val getVariantSet = (e: HgvsEntry) => e.gene.flatMap(api.hgvsPName(e.hgvs, _))

    entriesWithGene(cnameGeneEntries.map(e => (e, getVariant(e)))) ++
      entriesWithGeneSet(pnameGeneEntries.map(e => (e, getVariantSet(e).map(_.mapValues(applyEntryLimit)))))
  }

  def buildTranscriptCoords(api: Api): Seq[HgvsVcfOutcome] = {
    val getVariant = (e: HgvsEntry) => e.transcript.flatMap(api.hgvsCNameTranscript(e.hgvs, _))
    val getVariantSet = (e: HgvsEntry) => e.transcript.flatMap(api.hgvsPNameTranscript(e.hgvs, _))

    entriesWithTranscript(cnameTranscriptEntries.map(e => (e, getVariant(e)))) ++
      entriesWithTranscriptSet(pnameTranscriptEntries.map(e => (e, getVariantSet(e).map(applyEntryLimit))))
  }

  def buildMetaKeys(api: Api, unmatchedEntries: Seq[HgvsEntry] = Seq()): Seq[MetaKey] = {
    def combine(keys: Seq[MetaKey], e: (String, Seq[HgvsEntry])): Seq[MetaKey] = {
      val (k, xs) = e
      if (xs.isEmpty) keys
      else keys :+ MetaKey(k, xs.map(_.toVcfMetaValue).mkString(";"))
    }

    val init = Seq(
      MetaKey("PolyJuiceVersion", "0.1.0-SNAPSHOT"),
      MetaKey("EnsemblBuild", s"${api.ensemblBuild}"))

    Map(
      "UnmatchedEntries" -> unmatchedEntries,
      "MalformedEntries" -> errors).foldLeft(init)(combine)
  }

  def buildVcf(api: Api): Seq[VcfLine] = {
    val (_, trLines) = partitionOutcome(buildTranscriptCoords(api))
    val (_, geneLines) = partitionOutcome(buildGeneCoords(api))
    val allMatches = (trLines ++ geneLines).flatMap(addEntryAsInfoKey)
    val withInfoKeys = infoKeys.fold(allMatches)(keys => allMatches.map(appendInfoKeys(_, keys)))
    formatKeys.fold(withInfoKeys)(keys => withInfoKeys.map(appendFormatKeys(_, keys)))
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
    xs.map(x => x.copy(info = x.info + (key -> e.toVcfMetaValue)))
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
      case (e, Some(s)) if s.nonEmpty => Seq(Right((e, buildFromSet(e, s))))
      case (e, _)                     => Seq(Left(e))
    }
  }

  def entriesWithGene(xs: Seq[(HgvsEntry, Option[Map[Transcript, VariantCoord]])]): Seq[HgvsVcfOutcome] = {
    entriesWithGeneSet(xs.map {
      case (e, opt) => (e, opt.map(_.mapValues(Set(_))))
    })
  }

  def entriesWithGeneSet(xs: Seq[(HgvsEntry, Option[Map[Transcript, Set[VariantCoord]]])]): Seq[HgvsVcfOutcome] = {
    xs.flatMap {
      case (e, Some(m)) if m.nonEmpty => Seq(Right((e, toVcfLine(m))))
      case (e, _)                     => Seq(Left(e))
    }
  }

  def toVcfLine(transcript: Transcript, set: Set[VariantCoord]): Seq[VcfLine] = {
    set.toSeq.map(VcfLine(_, Seq(transcript)))
  }

  def toVcfLine(map: Map[Transcript, Set[VariantCoord]]): Seq[VcfLine] = {
    val invertedMap = map.toSeq.flatMap {
      case (tr, variants) => variants.seq.map(v => (v, tr))
    }
    invertedMap.groupBy(_._1).mapValues(_.map(_._2)).toSeq.map {
      case (variant, transcripts) => VcfLine(variant, transcripts)
    }
  }

  def appendInfoKeys(line: VcfLine, keys: Map[InfoKey, String]): VcfLine = {
    line.copy(info = line.info ++ keys)
  }

  def appendFormatKeys(line: VcfLine, keys: Map[FormatKey, String]): VcfLine = {
    line.copy(samples = Some(LineSamples(LineSamples.buildKey(keys), Seq(Sample(keys)))))
  }
}