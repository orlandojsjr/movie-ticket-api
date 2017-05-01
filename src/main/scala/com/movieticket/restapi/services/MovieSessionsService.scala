package com.movieticket.restapi.services

import com.movieticket.restapi.models.db.MovieSessionTable
import com.movieticket.restapi.models.{ MovieSession, MovieSessionRequest, MovieSessionAggr, Reserve }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MovieSessionsService extends MovieSessionsService

trait MovieSessionsService extends MovieSessionTable {

  import driver.api._

  def getSessions(movieId: String): Future[Seq[MovieSession]] = db.run(movieSessions.filter(_.imdbid === movieId).result)

  def getMovieSessionsBy(reserve: Reserve): Future[Option[MovieSession]] = db.run(movieSessions.filter(m => m.screenId === reserve.screenId && m.imdbid === reserve.imdbid).result.headOption)

  def createMovieSession(movie: MovieSession): Future[MovieSession] =
    db.run(movieSessions returning movieSessions += movie)

  def reserveSeat(reserve: Reserve): Future[Option[MovieSessionAggr]] =
    update(
      reserve,
      (session: MovieSession) => session.isThereAvailableSeat,
      (session: MovieSession) => session.copy(reservedSeats = session.reservedSeats + 1)
    )

  def cancelReserve(reserve: Reserve): Future[Option[MovieSessionAggr]] =
    update(
      reserve,
      (session: MovieSession) => session.isThereSeatReserved,
      (session: MovieSession) => session.copy(reservedSeats = session.reservedSeats - 1)
    )

  private def update(reserve: Reserve, canUpdate: MovieSession => Boolean, updateSession: MovieSession => MovieSession): Future[Option[MovieSessionAggr]] = getMovieSessionsBy(reserve).flatMap {
    case Some(session) =>
      if(canUpdate(session)) {
        val sessionUpdated = updateSession(session)
        db.run(movieSessions.filter(s => s.screenId === reserve.screenId && s.imdbid === reserve.imdbid)
          .update(sessionUpdated)).flatMap(_ => getMovieSessionsBy(reserve.screenId, reserve.imdbid))
      } else {
        Future.successful(None)
      }
    case None => Future.successful(None)
  }

  def getMovieSessionsBy(screenId: String, imdbid: String): Future[Option[MovieSessionAggr]] = {
    val result = db.run((
      for (
        (movie, sessions) <- movies join movieSessions if movie.imdbid === imdbid && sessions.screenId === screenId
      ) yield (movie, sessions)).result.headOption
    )

    result.map ( _.map {
      case (movie, session) => MovieSessionAggr(session.screenId, movie.imdbid, movie.title, session.availableSeats, session.reservedSeats)
    })
  }
}
