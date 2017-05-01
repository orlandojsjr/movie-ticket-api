package com.movieticket

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import org.scalatest.concurrent.ScalaFutures

import spray.json._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import com.movieticket.restapi.models.Movie

class MoviesRestTest extends BaseServiceTest with ScalaFutures {
  val newMovie = Movie(imdbid = "1234", title = "test")
  val currentMovieTest = testMovies.head

  "Movies rest" should {
    "retrieve movies list" in {
      Get("/movies") ~> moviesRoute ~> check {
        responseAs[JsArray] should be(testMovies.toJson)
      }
    }

    "retrieve movie by imdb" in {
      Get(s"/movies/${currentMovieTest.imdbid}") ~> moviesRoute ~> check {
        responseAs[JsObject] should be(currentMovieTest.toJson)
      }
    }

    "create a movie and retrieve it" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, JsObject("imdbid" -> JsString(newMovie.imdbid), "title" -> JsString(newMovie.title)).toString())
      Post("/movies/", requestEntity) ~> moviesRoute ~> check {
        response.status should be(StatusCodes.Created)
        val movieResponse = movieFormat.read(responseAs[JsValue])
        movieResponse.imdbid should be(newMovie.imdbid)
        movieResponse.title should be(newMovie.title)
      }
    }

    "update movie by id and retrieve it" in {
      val newMovieTitle = "new title"
      val requestEntity = HttpEntity(MediaTypes.`application/json`, JsObject("imdbid" -> JsString(newMovie.imdbid), "title" -> JsString(newMovieTitle)).toString())
      Put(s"/movies/${currentMovieTest.imdbid}", requestEntity) ~> moviesRoute ~> check {
        responseAs[JsObject] should be(currentMovieTest.copy(title = newMovieTitle).toJson)
        whenReady(getMovieById(currentMovieTest.imdbid)) { result =>
          result.get.title should be(newMovieTitle)
        }
      }
    }

    "delete movie" in {
      Delete(s"/movies/${currentMovieTest.imdbid}") ~> moviesRoute ~> check {
        response.status should be(NoContent)
        whenReady(getMovieById(currentMovieTest.imdbid)) { result =>
          result should be(None: Option[Movie])
        }
      }
    }

    "receive a bad request" in {
      val requestEntity = HttpEntity(MediaTypes.`application/json`, JsObject("wrong-param" -> JsString("bad request")).toString())
      Post("/movies/", requestEntity) ~> Route.seal(moviesRoute) ~> check {
        response.status should be(StatusCodes.BadRequest)
      }
    }
  }
}
