package fokot

import cats.effect.IO
import freestyle.free._
import freestyle.free.implicits._
import freestyle.free.http.http4s._
import fs2.StreamApp
import org.http4s._
import org.http4s.dsl.io._

import scala.math.BigDecimal
import org.http4s.server.blaze.BlazeBuilder


@free trait CalcVAT {
  def vat(price: BigDecimal): FS[BigDecimal]
  def withVat(price: BigDecimal): FS[BigDecimal] =
    vat(price).map(_ + price)
}

object Price {
  def unapply(s: String): Option[BigDecimal] =
    if (s.isEmpty) None else Some(BigDecimal(s))
}


object Main extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val calcIoHandler = new CalcVAT.Handler[IO] {

    private val rate: BigDecimal = BigDecimal(20) / BigDecimal(100)

    def vat(price: BigDecimal): IO[BigDecimal] =
      IO.pure(price * rate)
  }

  val userService = HttpService[IO] {
    case GET -> Root / "calc-vat" / Price(p) =>
      Ok(CalcVAT[CalcVAT.Op].vat(p).map(vat => s"The VAT for $p is $vat"))
    case GET -> Root / "calc-total" / Price(p) =>
      Ok(CalcVAT[CalcVAT.Op].withVat(p).map(total => s"The total price including VAT is $total"))
  }


  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO].bindHttp(8080, "0.0.0.0")
      .mountService(userService, "/")
      .serve
}