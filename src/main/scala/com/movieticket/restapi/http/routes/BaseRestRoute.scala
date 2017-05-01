package com.movieticket.restapi.http.routes

import akka.event.LoggingAdapter
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import com.movieticket.restapi.utils.{ Config, Protocol }

import scala.concurrent.ExecutionContext
import com.movieticket.restapi.http.Resource

trait BaseServiceRoute extends Protocol with SprayJsonSupport with Config with Resource {
  protected implicit def executor: ExecutionContext
  protected implicit def materializer: ActorMaterializer
  protected def log: LoggingAdapter
}
