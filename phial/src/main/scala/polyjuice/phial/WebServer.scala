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

  // custom encoder
  implicit val baseEncoder = new Encoder[Base] {
    final def apply(b: Base): Json = Json.fromString(s"$b")
  }

  val api = Api(Loader.init)

  val service = HttpService[IO] {

    case GET -> Root / "gene" / geneSymbol =>
      api.getGene(geneSymbol).fold(NotFound(geneSymbol))(gene => Ok(gene.asJson))

    case GET -> Root / "gene" / geneSymbol / "cds" / "pos" / IntVar(pos) =>
      api.cdsPos(geneSymbol, pos).fold(NotFound(geneSymbol))(map => Ok(map.asJson))

    case GET -> Root / "gene" / geneSymbol / "codon" / "pos" / IntVar(pos) =>
      api.codonPos(geneSymbol, pos).fold(NotFound(geneSymbol))(map => Ok(map.asJson))

    case GET -> Root / "gene" / geneSymbol / "exon" / "num" / IntVar(num) =>
      api.exonNum(geneSymbol, num).fold(NotFound(geneSymbol))(map => Ok(map.asJson))

    case GET -> Root / "gene" / geneSymbol / "hgvs" / "c" / cname =>
      Ok()
    case GET -> Root / "gene" / geneSymbol / "hgvs" / "p" / pname =>
      Ok()

    case GET -> Root / "transcript" / transcript =>
      api.getTranscript(transcript).fold(NotFound(transcript))(gene => Ok(gene.asJson))

    case GET -> Root / "transcript" / transcript / "cds" / "pos" / IntVar(pos) =>
      api.cdsPosTranscript(transcript, pos).fold(NotFound(transcript))(map => Ok(map.asJson))

    case GET -> Root / "transcript" / transcript / "codon" / "pos" / IntVar(pos) =>
      api.codonPosTranscript(transcript, pos).fold(NotFound(transcript))(map => Ok(map.asJson))

    case GET -> Root / "transcript" / transcript / "exon" / "pos" / IntVar(num) =>
      api.exonNumTranscript(transcript, num).fold(NotFound(transcript))(map => Ok(map.asJson))

    case GET -> Root / "transcript" / transcript / "hgvs" / "c" / cname =>
      Ok()
    case GET -> Root / "transcript" / transcript / "hgvs" / "p" / pname =>
      Ok()
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(service, "/api/polyjuice")
      .serve
}