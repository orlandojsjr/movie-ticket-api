package com.movieticket.restapi.models.db

import com.movieticket.restapi.models.Movie
import com.movieticket.restapi.utils.DatabaseConfig

trait MovieTable extends DatabaseConfig {

  import driver.api._

  class Movies(tag: Tag) extends Table[Movie](tag, "movies") {
    def imdbid = column[String]("imdbid", O.PrimaryKey)
    def title = column[String]("title")

    def * = (imdbid, title) <> ((Movie.apply _).tupled, Movie.unapply)
  }

  protected val movies = TableQuery[Movies]

}
