package com.kusius.movies.database

import com.kusius.movies.database.model.CastEntity
import com.kusius.movies.database.model.CrewEntity
import com.kusius.movies.database.model.GenreEntity
import com.kusius.movies.database.model.MovieEntity

object TestUtil {
    fun createMovie(amount: Int): List<MovieEntity> {
        return List(amount) {
            MovieEntity(
                movieId = it,
                isFavourite = false,
                isSearchResult = false,
                popularity = 3.0f,
                posterUrl = "",
                ratingOutOf10 = 4f,
                title = "Movie $it",
                releaseDate = "$it Mar ${2000+it}"
            )
        }
    }

    fun createCastForMovie(movie: MovieEntity, castNumber: Int): List<CastEntity> {
        return List(castNumber) {
            CastEntity(
                castMovieId = movie.movieId,
                name = "Cast Member $it"
            )
        }
    }

    fun createCrewForMovie(movie: MovieEntity, crewNumber: Int): List<CrewEntity> {
        return List(crewNumber) {
            CrewEntity(
                crewMovieId = movie.movieId,
                job = "Producer",
                name = "Crew Member $it"
            )
        }
    }

    fun createGenres(amount: Int): List<GenreEntity> {
        return List(amount) {
            GenreEntity(
                genreId = it,
                name = "Genre $it"
            )
        }
    }
}