package com.movieticket.restapi.http

import akka.http.scaladsl.server.Directives._
import com.movieticket.restapi.http.routes._
import com.movieticket.restapi.utils.CorsSupport

trait HttpService extends MoviesServiceRoute with MovieSessionsServiceRoute with CorsSupport {

  val routes =
    pathPrefix("v1") {
      corsHandler {
        moviesRoute ~
          movieSessionsRoute
      }
    }

}
