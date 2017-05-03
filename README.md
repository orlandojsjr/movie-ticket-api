Movie Ticket API 
=========================


[![Build Status](https://travis-ci.org/orlandojsjr/movie-ticket.svg?branch=travis)](https://travis-ci.org/orlandojsjr/movie-ticket)

Typesafe stack with Akka and Slick.
REST API for to manage reserves in a movie session

### Features:
* CRUD operations for a movie
* Create a session for a movie
* Reserve a seat for a session
* Cancel a reserve seat for a session

## API Documentarion
On [Apiary](http://docs.movieticketapi.apiary.io/#)

## In Action
Application deployed on heroku and can be accessed by URL [http://movie-ticket-api.herokuapp.com/](https://movie-ticket-api.herokuapp.com/v1/movies). First request can take some time, because heroku launch up project.

## Run application
To run application, call:
```
sbt run
```

## Run tests
To run tests, call:
```
sbt test
```
