package com.workable.feature.details.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import com.workable.feature.details.ui.PreviewParameterData.movie
import com.workable.feature.details.ui.PreviewParameterData.similarMovies
import com.workable.movierama.core.model.MovieDetails
import com.workable.movierama.core.model.MovieGenre
import com.workable.movierama.core.model.MovieReview
import com.workable.movierama.core.model.MovieSimilar
import com.workable.movierama.core.model.MovieSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class MovieDetailsScreenParams(
    val movieDetails: MovieDetails,
    val similarMovies: Flow<PagingData<MovieSummary>>
)

class MovieDetailsPreviewParameterProvider : PreviewParameterProvider<MovieDetailsScreenParams> {
    override val values: Sequence<MovieDetailsScreenParams> = sequenceOf(
        MovieDetailsScreenParams(
            movieDetails = movie,
            similarMovies = similarMovies
        ))
}

class SimilarMoviesPreviewParameterProvider : PreviewParameterProvider<LazyPagingItems<MovieSummary>> {
    override val values: Sequence<LazyPagingItems<MovieSummary>> = sequenceOf()
}


object PreviewParameterData {
    val movie = MovieDetails(
        summary = MovieSummary(
            id = 500,
            title = "Movie Title",
            posterUrl = "https://placehold.co/600x400",
            isFavourite = false,
            releaseDate = "",
            ratingOutOf10 = 4.4f
        ),
        genres = listOf(MovieGenre(id = 1, name = "Thriller"), MovieGenre(id = 2, name = "Comedy"), MovieGenre(id = 2, name = "Drama"), MovieGenre(id = 4, name = "Sci-Fi")),
        overview = "This is a nice movie with a nice plot!",
        directorName = "Jeffrey Bezos",
        castNames = listOf("Jennifer Ariston", "Elon Musketeer"),
        reviews = listOf(
            MovieReview(authorName = "Random Guy", content = "Did not like"),
            MovieReview(authorName = "Random Girl", content = "Very boring"),
        ),
    )

    val similarMovies = flow<PagingData<MovieSummary>> {PagingData.empty<MovieSummary>()}
}