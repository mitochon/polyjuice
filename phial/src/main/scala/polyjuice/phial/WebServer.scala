package polyjuice.phial

import scala.concurrent.ExecutionContext.Implicits.global

import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._

import cats.effect._

import fs2.{ Stream, StreamApp }
import fs2.StreamApp.ExitCode

object WebServer extends StreamApp[IO] {

  val service = HttpService[IO] {
    case GET -> Root / "gene" / gene =>
      Ok()
    case GET -> Root / "gene" / gene / "cds" / "pos" / pos =>
      Ok()
    case GET -> Root / "gene" / gene / "codon" / "pos" / pos =>
      Ok()
    case GET -> Root / "gene" / gene / "exon" / "pos" / pos =>
      Ok()
    case GET -> Root / "gene" / gene / "hgvs" / "c" / cname =>
      Ok()
    case GET -> Root / "gene" / gene / "hgvs" / "p" / pname =>
      Ok()
    case GET -> Root / "transcript" / transcript =>
      Ok()
    case GET -> Root / "transcript" / transcript / "cds" / "pos" / pos =>
      Ok()
    case GET -> Root / "transcript" / transcript / "codon" / "pos" / pos =>
      Ok()
    case GET -> Root / "transcript" / transcript / "exon" / "pos" / pos =>
      Ok()
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