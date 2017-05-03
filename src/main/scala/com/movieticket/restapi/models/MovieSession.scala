package com.movieticket.restapi.models

case class MovieSessionInfo(screenId: String, imdbid: String, title: String, availableSeats: Long, reservedSeats: Long, links: Seq[Relation])
case class MovieSessionRequest(screenId: String, imdbid: String, availableSeats: Long) {
  def toMovieSession = MovieSession(None, this.screenId, this.imdbid, this.availableSeats)
}

case class MovieSessionAggr(screenId: String, imdbid: String, title: String, availableSeats: Long, reservedSeats: Long)
case class MovieSession(id: Option[Long] = None, screenId: String, imdbid: String, availableSeats: Long, reservedSeats: Long = 0) {
  def isThereAvailableSeat: Boolean = availableSeats > reservedSeats
  def isThereSeatReserved: Boolean = reservedSeats > 0
}

case class Reserve(screenId: String, imdbid: String)
