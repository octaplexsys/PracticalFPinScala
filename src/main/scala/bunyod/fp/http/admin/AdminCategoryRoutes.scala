package bunyod.fp.http.admin

import bunyod.fp.domain.categories.CategoriesService
import bunyod.fp.domain.categories.CategoryPayloads.CategoryParam
import bunyod.fp.domain.users.UsersPayloads.AdminUser
import bunyod.fp.effekts.MonadThrow
import cats.Defer
import org.http4s.{AuthedRoutes, HttpRoutes}
import bunyod.fp.http.utils.decoder._
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl

final class AdminCategoryRoutes[F[_]: Defer: JsonDecoder: MonadThrow](categories: CategoriesService[F])
  extends Http4sDsl[F] {

  private[admin] val pathPrefix = "/categories"

  private val httpRoutes: AuthedRoutes[AdminUser, F] =
    AuthedRoutes.of { case ar @ POST -> Root as _ =>
      ar.req.decodeR[CategoryParam](c => Created(categories.create(c.toDomain)))
    }

  def routes(authMiddleware: AuthMiddleware[F, AdminUser]): HttpRoutes[F] = Router(
    pathPrefix -> authMiddleware(httpRoutes)
  )
}
