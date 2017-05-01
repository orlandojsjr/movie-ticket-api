package com.movieticket.restapi.services

import com.movieticket.restapi.models.db.MovieTable
import com.movieticket.restapi.models.Movie

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MoviesService extends MoviesService

trait MoviesService extends MovieTable {

  import driver.api._

  def getMovies(): Future[Seq[Movie]] = db.run(movies.result)

  def getMovieById(id: String): Future[Option[Movie]] = db.run(movies.filter(_.imdbid === id).result.headOption)

  def createMovie(movie: Movie): Future[Movie] = db.run(movies returning movies += movie)

  def updateMovie(imdb: String, movieUpdate: Movie): Future[Option[Movie]] = getMovieById(imdb).flatMap {
    case Some(movie) =>
      val updatedMovie = movie.copy(title = movieUpdate.title)
      db.run(movies.filter(_.imdbid === imdb).update(updatedMovie)).map(_ => Some(updatedMovie))
    case None => Future.successful(None)
  }

  def deleteMovie(imdb: String): Future[Int] = db.run(movies.filter(_.imdbid === imdb).delete)

}
