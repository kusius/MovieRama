package com.workable.core.data.model

import com.workable.movierama.core.network.model.NetworkMovie
import com.workable.movierama.database.model.MovieEntity

// Contains useful transformations for the data layer related to API and DB models

/**
 * Converts a movie as received from the network into a database entity.
  */
fun NetworkMovie.asEntity(isFavourite: Boolean, isSearchResult: Boolean) = MovieEntity(
    id = id,
    posterUrl = backdropUrl,
    title = title,
    ratingOutOf10 = rating,
    releaseDate = releaseDate,
    isFavourite = isFavourite,
    popularity = popularity,
    isSearchResult = isSearchResult
)