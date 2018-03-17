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
      api.hgvsCName(cname, geneSymbol).fold(NotFound(geneSymbol))(map => Ok(map.asJson))

    case GET -> Root / "gene" / geneSymbol / "hgvs" / "p" / pname =>
      api.hgvsPName(pname, geneSymbol).fold(NotFound(geneSymbol))(map => Ok(map.asJson))

    case GET -> Root / "transcript" / transcript =>
      api.getTranscript(transcript).fold(NotFound(transcript))(gene => Ok(gene.asJson))

    case GET -> Root / "transcript" / transcript / "cds" / "pos" / IntVar(pos) =>
      api.cdsPosTranscript(transcript, pos).fold(NotFound(transcript))(base => Ok(base.asJson))

    case GET -> Root / "transcript" / transcript / "codon" / "pos" / IntVar(pos) =>
      api.codonPosTranscript(transcript, pos).fold(NotFound(transcript))(codon => Ok(codon.asJson))

    case GET -> Root / "transcript" / transcript / "exon" / "pos" / IntVar(num) =>
      api.exonNumTranscript(transcript, num).fold(NotFound(transcript))(exon => Ok(exon.asJson))

    case GET -> Root / "transcript" / transcript / "hgvs" / "c" / cname =>
      api.hgvsCNameTranscript(cname, transcript).fold(NotFound(transcript))(snv => Ok(snv.asJson))

    case GET -> Root / "transcript" / transcript / "hgvs" / "p" / pname =>
      api.hgvsPNameTranscript(pname, transcript).fold(NotFound(transcript))(set => Ok(set.asJson))

    case GET -> Root / "hgvs" / "p" / pname =>
      api.hgvsPName(pname).fold(NotFound(pname))(p => Ok(p.asJson))

    case GET -> Root / "hgvs" / "c" / cname =>
      api.hgvsCName(cname).fold(NotFound(cname))(c => Ok(c.asJson))
  }

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(service, "/api/polyjuice")
      .serve
}