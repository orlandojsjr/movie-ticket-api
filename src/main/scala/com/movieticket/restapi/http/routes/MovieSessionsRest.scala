package com.movieticket.restapi.http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.PathMatchers.Segment
import com.movieticket.restapi.models._
import com.movieticket.restapi.services.MovieSessionsService

import spray.json._

trait MovieSessionsServiceRoute extends MovieSessionsService with BaseServiceRoute {

  import StatusCodes._

  val movieSessionsRoute = pathPrefix("movies" / Segment / "sessions") { imdbid =>
    pathEndOrSingleSlash {
      get {
        complete(getSessions(imdbid).map(_.toJson))
      } ~
        post {
           entity(as[MovieSessionRequest]) { session =>
             complete(Created -> createMovieSession(MovieSession(None, session.screenId, session.imdbid, session.availableSeats)).map(_.toJson))
           }
        }
    } ~
    pathPrefix(Segment) { screenId =>
      get {
          complete(getMovieSessionsBy(screenId, imdbid))
      } ~
        pathPrefix("reserve") {
          pathEndOrSingleSlash {
            post {
              entity(as[Reserve]) { reserve =>
                complete(reserveSeat(reserve))
              }
            } ~
              delete {
                entity(as[Reserve]) { reserve =>
                  complete(cancelReserve(reserve))
                }
              }
          }
        }
    }
  }
}
