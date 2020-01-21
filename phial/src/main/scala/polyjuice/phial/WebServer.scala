package polyjuice.phial

import scala.concurrent.ExecutionContext.Implicits.global
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import cats.effect._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import fs2.{ Stream, StreamApp }
import fs2.StreamApp.ExitCode
import polyjuice.phial.model.{ Hgvs2VcfRequest, HgvsEntry, Status }
import polyjuice.potion.model._
import org.http4s.server.blaze.BlazeServerBuilder

object WebServer extends StreamApp[IO] {

  // enum encoder
  implicit val strandEncoder = Encoder.enumEncoder(Strand)
  implicit val codonPhaseEncoder = Encoder.enumEncoder(CodonPhase)
  implicit val aminoAcidCodeEncoder = Encoder.enumEncoder(AminoAcid.Code)

  // custom encoder
  implicit val baseEncoder = new Encoder[Base] {
    final def apply(b: Base): Json = Json.fromString(s"$b")
  }

  // decoder
  implicit val hgvs2vcfDecoder = jsonOf[IO, Hgvs2VcfRequest]

  // request param extractors
  object GeneSymbolVar {
    def unapply(g: String): Option[GeneSymbol] = {
      Option(g).map(_.toUpperCase)
    }
  }

  object TranscriptVar {
    def unapply(t: String): Option[Transcript] = {
      // uppercase and drop build
      Option(t).map(_.toUpperCase).filter(_.startsWith("ENST")).map(_.takeWhile(_ != '.'))
    }
  }

  val api = Api(Loader.init, WebServerConfig.EnsemblBuild)

  val status = Status(api.ensemblBuild, api.genes.keySet.toList.sorted)

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "status" =>
      Ok(status.asJson)

    case GET -> Root / "gene" / GeneSymbolVar(geneSymbol) =>
      resp(geneSymbol, api.getGene(geneSymbol))

    case GET -> Root / "gene" / GeneSymbolVar(geneSymbol) / "cds" / "pos" / IntVar(pos) =>
      resp(geneSymbol, api.cdsPos(geneSymbol, pos))

    case GET -> Root / "gene" / GeneSymbolVar(geneSymbol) / "cds" / "coord" / IntVar(pos) =>
      resp(geneSymbol, api.cdsCoord(geneSymbol, pos))

    case GET -> Root / "gene" / GeneSymbolVar(geneSymbol) / "codon" / "pos" / IntVar(pos) =>
      resp(geneSymbol, api.codonPos(geneSymbol, pos))

    case GET -> Root / "gene" / GeneSymbolVar(geneSymbol) / "codon" / "coord" / IntVar(pos) =>
      resp(geneSymbol, api.codonCoord(geneSymbol, pos))

    case GET -> Root / "gene" / GeneSymbolVar(geneSymbol) / "exon" / "num" / IntVar(num) =>
      resp(geneSymbol, api.exonNum(geneSymbol, num))

    case GET -> Root / "gene" / GeneSymbolVar(geneSymbol) / "hgvs" / hgvs =>
      resp(hgvs, cond(HgvsEntry.IsPName(hgvs), api.hgvsPName(hgvs, geneSymbol))
        .orElse(cond(HgvsEntry.IsCName(hgvs), api.hgvsCName(hgvs, geneSymbol))))

    case GET -> Root / "transcript" / TranscriptVar(transcript) =>
      resp(transcript, api.getTranscript(transcript))

    case GET -> Root / "transcript" / TranscriptVar(transcript) / "cds" / "pos" / IntVar(pos) =>
      resp(transcript, api.cdsTranscriptPos(transcript, pos))

    case GET -> Root / "transcript" / TranscriptVar(transcript) / "cds" / "coord" / IntVar(pos) =>
      resp(transcript, api.cdsTranscriptCoord(transcript, pos))

    case GET -> Root / "transcript" / TranscriptVar(transcript) / "codon" / "pos" / IntVar(pos) =>
      resp(transcript, api.codonTranscriptPos(transcript, pos))

    case GET -> Root / "transcript" / TranscriptVar(transcript) / "codon" / "coord" / IntVar(pos) =>
      resp(transcript, api.codonTranscriptCoord(transcript, pos))

    case GET -> Root / "transcript" / TranscriptVar(transcript) / "exon" / "num" / IntVar(num) =>
      resp(transcript, api.exonNumTranscript(transcript, num))

    case GET -> Root / "transcript" / TranscriptVar(transcript) / "hgvs" / hgvs =>
      resp(hgvs, cond(HgvsEntry.IsPName(hgvs), api.hgvsPNameTranscript(hgvs, transcript))
        .orElse(cond(HgvsEntry.IsCName(hgvs), api.hgvsCNameTranscript(hgvs, transcript))))

    case GET -> Root / "hgvscheck" / hgvs =>
      resp(hgvs, cond(HgvsEntry.IsPName(hgvs), api.hgvsPName(hgvs))
        .orElse(cond(HgvsEntry.IsCName(hgvs), api.hgvsCName(hgvs))))

    case req @ POST -> Root / "hgvs2vcf" =>
      for {
        vcf <- req.as[Hgvs2VcfRequest]
        res <- Ok(api.hgvs2vcf(vcf))
      } yield res
  }

  def resp[A](err: String, body: Option[A])(implicit encoder: Encoder[A]) = {
    body.map(_.asJson).fold(NotFound(err))(json => Ok(json))
  }

  def cond[A](predicate: Boolean, exec: => Option[A])(implicit encoder: Encoder[A]): Option[Json] = {
    if (predicate) exec.map(_.asJson) else None
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(WebServerConfig.ServicePort, WebServerConfig.ServiceHost)
      .mountService(service, "/api/polyjuice")
      .serve
}