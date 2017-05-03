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
             complete(Created -> createMovieSession(session.toMovieSession).map(_.map(buildResponse(_).toJson)))
           }
        }
    } ~
    pathPrefix(Segment) { screenId =>
      get {
          complete(getMovieSessionsBy(screenId, imdbid).map(_.map(buildResponse(_))))
      } ~
        pathPrefix("reserve") {
          pathEndOrSingleSlash {
            post {
              entity(as[Reserve]) { reserve =>
                complete(reserveSeat(reserve).map(_.map(buildResponse(_))))
              }
            } ~
              delete {
                entity(as[Reserve]) { reserve =>
                  complete(cancelReserve(reserve).map(_.map(buildResponse(_))))
                }
              }
          }
        }
    }
  }

  def buildResponse(session: MovieSessionAggr): MovieSessionInfo =
    MovieSessionInfo(session.screenId, session.imdbid, session.title, session.availableSeats, session.reservedSeats, relations(session))

  def relations(session: MovieSessionAggr): Seq[Relation] = Seq(
    Relation("self", "GET", s"/v1/movies/${session.imdbid}/sessions/${session.screenId}"),
    Relation("reserve", "POST", s"/v1/movies/${session.imdbid}/sessions/${session.screenId}/reserve"),
    Relation("cancel_reserve", "DELETE", s"/v1/movies/${session.imdbid}/sessions/${session.screenId}/reserve")
  )
}
