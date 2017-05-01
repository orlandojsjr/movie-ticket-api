package com.movieticket

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import org.scalatest.concurrent.ScalaFutures

import spray.json._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import com.movieticket.restapi.models.{ Movie, MovieSessionRequest, MovieSessionAggr, Reserve }

class MoviesSessionRestTest extends BaseServiceTest with ScalaFutures {
  val newSession = MovieSessionRequest(screenId = "1A", imdbid = "tt0111161", availableSeats = 50)

  val currentMovieTest = testMovies.head
  val currentMovieSessionTest = testMovieSessions.head

  val movieSessionAggr =
    MovieSessionAggr(screenId = currentMovieSessionTest.screenId,
      imdbid = currentMovieTest.imdbid,
      title = currentMovieTest.title,
      availableSeats = currentMovieSessionTest.availableSeats,
      reservedSeats = 0l)

  val path = s"/movies/${currentMovieSessionTest.imdbid}/sessions"

  "Movie Session rest" should {
    "retrieve movie sessions list" in {
      Get(path) ~> movieSessionsRoute ~> check {
        response.status should be(StatusCodes.OK)
        responseAs[JsArray].toString().contains(currentMovieSessionTest.screenId) should be(true)
      }
    }

    "retrieve movie session by screenId and imdbid" in {
      Get(s"${path}/${currentMovieSessionTest.screenId}") ~> movieSessionsRoute ~> check {
        responseAs[JsObject] should be(movieSessionAggr.toJson)
        val sessionResponse = movieSessionResponseFormat.read(responseAs[JsValue])
        sessionResponse.imdbid should be(movieSessionAggr.imdbid)
        sessionResponse.screenId should be(movieSessionAggr.screenId)
        sessionResponse.title should be(movieSessionAggr.title)
        sessionResponse.availableSeats should be(movieSessionAggr.availableSeats)
        sessionResponse.reservedSeats should be(movieSessionAggr.reservedSeats)
      }
    }

    "create a movie session and retrieve it" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, JsObject("screenId" -> JsString(newSession.screenId), "imdbid" -> JsString(newSession.imdbid), "availableSeats" -> JsNumber(newSession.availableSeats)).toString())
      Post(path, requestEntity) ~> movieSessionsRoute ~> check {
        response.status should be(StatusCodes.Created)
        val movieSessionResponse = movieSessionFormat.read(responseAs[JsValue])
        movieSessionResponse.screenId should be(newSession.screenId)
        movieSessionResponse.imdbid should be(newSession.imdbid)
        movieSessionResponse.availableSeats should be(newSession.availableSeats)
      }
    }

    "make a reserve in a movie session" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, JsObject("screenId" -> JsString(reserve.screenId), "imdbid" -> JsString(reserve.imdbid)).toString())
      Post(s"${path}/${reserve.screenId}/reserve", requestEntity) ~> movieSessionsRoute ~> check {
        val movieSessionResponse = movieSessionFormat.read(responseAs[JsValue])
        movieSessionResponse.screenId should be(reserve.screenId)
        movieSessionResponse.imdbid should be(reserve.imdbid)
        movieSessionResponse.reservedSeats should be(1l)
      }
    }

    "try to make a reserve in a FULL movie session and get a NOTFOUND response" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, JsObject("screenId" -> JsString(reserveFull.screenId), "imdbid" -> JsString(reserveFull.imdbid)).toString())
      Post(s"${path}/${reserveFull.screenId}/reserve", requestEntity) ~> movieSessionsRoute ~> check {
        response.status should be(StatusCodes.NotFound)
      }
    }

    "cancel a reserve in a movie session" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, JsObject("screenId" -> JsString(reserveFull.screenId), "imdbid" -> JsString(reserveFull.imdbid)).toString())
      Delete(s"${path}/${reserveFull.screenId}/reserve", requestEntity) ~> movieSessionsRoute ~> check {
        val movieSessionResponse = movieSessionFormat.read(responseAs[JsValue])
        movieSessionResponse.screenId should be(reserveFull.screenId)
        movieSessionResponse.imdbid should be(reserveFull.imdbid)
        movieSessionResponse.reservedSeats should be(99)
      }
    }
  }
}
