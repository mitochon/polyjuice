package polyjuice.phial

import scala.concurrent.ExecutionContext.Implicits.global

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._

import cats.effect._

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import fs2.{ Stream, StreamApp }
import fs2.StreamApp.ExitCode

import polyjuice.potion.model._

object WebServer extends StreamApp[IO] {

  // enum encoder
  implicit val strandEncoder = Encoder.enumEncoder(Strand)
  implicit val codonPhaseEncoder = Encoder.enumEncoder(CodonPhase)
  implicit val aminoAcidCodeEncoder = Encoder.enumEncoder(AminoAcid.Code)

  // custom encoder
  implicit val baseEncoder = new Encoder[Base] {
    final def apply(b: Base): Json = Json.fromString(s"$b")
  }

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

  // helper functions
  val isPName = (hgvs: String) => hgvs.startsWith("p.")
  val isCName = (hgvs: String) => hgvs.startsWith("c.")

  val api = Api(Loader.init)

  val service = HttpService[IO] {

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
      resp(hgvs, cond(isPName(hgvs), api.hgvsPName(hgvs, geneSymbol))
        .orElse(cond(isCName(hgvs), api.hgvsCName(hgvs, geneSymbol))))

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

    case GET -> Root / "transcript" / TranscriptVar(transcript) / "exon" / "pos" / IntVar(num) =>
      resp(transcript, api.exonNumTranscript(transcript, num))

    case GET -> Root / "transcript" / TranscriptVar(transcript) / "hgvs" / hgvs =>
      resp(hgvs, cond(isPName(hgvs), api.hgvsPNameTranscript(hgvs, transcript))
        .orElse(cond(isCName(hgvs), api.hgvsCNameTranscript(hgvs, transcript))))

    case GET -> Root / "hgvscheck" / hgvs =>
      resp(hgvs, cond(isPName(hgvs), api.hgvsPName(hgvs))
        .orElse(cond(isCName(hgvs), api.hgvsCName(hgvs))))
  }

  def resp[A](err: String, body: Option[A])(implicit encoder: Encoder[A]) = {
    body.map(_.asJson).fold(NotFound(err))(json => Ok(json))
  }

  def cond[A](predicate: Boolean, exec: => Option[A])(implicit encoder: Encoder[A]): Option[Json] = {
    if (predicate) exec.map(_.asJson) else None
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(service, "/api/polyjuice")
      .serve
}