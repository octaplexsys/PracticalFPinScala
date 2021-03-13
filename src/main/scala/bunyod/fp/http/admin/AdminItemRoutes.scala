package bunyod.fp.http.admin

import bunyod.fp.domain.items._
import bunyod.fp.domain.items.ItemsPayloads._
import bunyod.fp.domain.users.UsersPayloads.AdminUser
import bunyod.fp.effekts.MonadThrow
import cats.Defer
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import bunyod.fp.http.utils.decoder._
import org.http4s.server._

final class AdminItemRoutes[F[_]: Defer: JsonDecoder: MonadThrow](
  items: ItemsService[F]
) extends Http4sDsl[F] {

  private[admin] val pathPrefix = "/items"

  private val httpRoutes: AuthedRoutes[AdminUser, F] =
    AuthedRoutes.of {
      case ar @ POST -> Root as _ =>
        ar.req.decodeR[CreateItemParam](item => Created(items.create(item.toDomain)))
      case ar @ PUT -> Root as _ =>
        ar.req.decodeR[UpdateItemParam](item => Ok(items.update(item.toDomain)))
    }

  def routes(authMiddleware: AuthMiddleware[F, AdminUser]): HttpRoutes[F] = Router(
    pathPrefix -> authMiddleware(httpRoutes)
  )
}
