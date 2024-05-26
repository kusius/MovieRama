package com.kusius.core.data.model

import com.kusius.movies.core.network.model.NetworkMovie
import com.kusius.movies.core.network.model.NetworkMovieCast
import com.kusius.movies.core.network.model.NetworkMovieCrew
import com.kusius.movies.core.network.model.NetworkMovieGenre
import com.kusius.movies.core.network.model.NetworkMovieReview
import com.kusius.movies.database.model.CastEntity
import com.kusius.movies.database.model.CrewEntity
import com.kusius.movies.database.model.GenreEntity
import com.kusius.movies.database.model.MovieEntity
import com.kusius.movies.database.model.MovieGenreCrossRef
import com.kusius.movies.database.model.ReviewEntity

// Contains useful transformations for the data layer related to API and DB models

/**
 * Converts a movie as received from the network into a database entity.
  */
fun NetworkMovie.asEntity(isFavourite: Boolean, isSearchResult: Boolean) = MovieEntity(
    movieId = id,
    posterUrl = backdropUrl,
    title = title,
    ratingOutOf10 = rating,
    releaseDate = releaseDate,
    isFavourite = isFavourite,
    popularity = popularity,
    isSearchResult = isSearchResult
)

fun NetworkMovieGenre.asEntity() = GenreEntity (
    genreId = this.id,
    name = this.name
)

fun NetworkMovieGenre.asCrossRef(movieId: Int) = MovieGenreCrossRef (
    genreId = this.id,
    movieId = movieId
)

fun NetworkMovieCast.asEntity(movieId: Int) = CastEntity (
    castMovieId = movieId,
    name = this.name
)

fun NetworkMovieCrew.asEntity(movieId: Int) = CrewEntity(
    crewMovieId = movieId,
    job = this.job,
    name = this.name
)

fun NetworkMovieReview.asEntity(movieId: Int) = ReviewEntity(
    reviewMovieId = movieId,
    author = this.author,
    content = this.content
)