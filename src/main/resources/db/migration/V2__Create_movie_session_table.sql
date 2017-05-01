CREATE TABLE "movie_sessions" (
  "id"       BIGSERIAL PRIMARY KEY,
  "screen_id" VARCHAR NOT NULL,
  "imdbid" VARCHAR NOT NULL,
  "available_seats" BIGINT NOT NULL,
  "reserved_seats" BIGINT
);
ALTER TABLE "movie_sessions" ADD CONSTRAINT "MOVIE_FK" FOREIGN KEY ("imdbid") REFERENCES "movies" ("imdbid") ON UPDATE RESTRICT ON DELETE CASCADE;