package com.movieticket.restapi

import akka.actor.ActorSystem
import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import com.movieticket.restapi.http.HttpService
import com.movieticket.restapi.utils.{ Migration, Config }

import scala.concurrent.ExecutionContext

object Main extends App with Config with HttpService with Migration {
  private implicit val system = ActorSystem()

  override protected implicit val executor: ExecutionContext = system.dispatcher
  override protected val log: LoggingAdapter = Logging(system, getClass)
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  migrate()

  Http().bindAndHandle(routes, httpInterface, httpPort)
}
