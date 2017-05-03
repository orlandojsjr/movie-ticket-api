package com.movieticket.restapi.utils

import com.movieticket.restapi.models._

import spray.json.DefaultJsonProtocol

trait Protocol extends DefaultJsonProtocol {
  implicit val relationFormat = jsonFormat3(Relation)
  implicit val movieSessionFormat = jsonFormat5(MovieSession)
  implicit val movieSessionRequestFormat = jsonFormat3(MovieSessionRequest)
  implicit val movieFormat = jsonFormat2(Movie)
  implicit val reserveSeatFormat = jsonFormat2(Reserve)
  implicit val movieInfoFormat = jsonFormat3(MovieInfo)
  implicit val movieSessionInfoFormat = jsonFormat6(MovieSessionInfo)

}
