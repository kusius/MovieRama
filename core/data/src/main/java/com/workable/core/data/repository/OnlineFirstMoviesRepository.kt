package com.workable.core.data.repository

import androidx.paging.PagingSource
import com.workable.core.data.paging.MoviesPagingSource
import com.workable.core.data.paging.PersistentSearchPagingSource
import com.workable.core.data.paging.SearchPagingKey
import com.workable.core.data.paging.SearchPagingSource
import com.workable.core.data.paging.SimilarMoviesPagingKey
import com.workable.core.data.paging.SimilarMoviesPagingSource
import com.workable.movierama.core.model.MovieDetails
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.core.network.model.NetworkMovieGenre
import com.workable.movierama.core.network.model.NetworkMovieReview
import com.workable.movierama.core.network.model.asExternalModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class OnlineFirstMoviesRepository(
    private val networkDataSource: MovieNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : PagingMoviesRepository {
    private val favouriteMovies = mutableSetOf<Int>()
    override fun getPopularMoviesPagingSource(): PagingSource<Int, MovieSummary> {
        return MoviesPagingSource(networkDataSource)
    }

    override fun getSearchResultsPagingSource(query: String): PagingSource<SearchPagingKey, MovieSummary> {
        return PersistentSearchPagingSource(query, networkDataSource)
    }

    override fun getSimilarMoviesPagingSource(movieId: Int): PagingSource<SimilarMoviesPagingKey, MovieSummary> {
        return SimilarMoviesPagingSource(movieId = movieId, networkDataSource)
    }

    override suspend fun markFavourite(movieId: Int, isFavourite: Boolean) {
        if (isFavourite) {
            favouriteMovies.add(movieId)
        } else {
            favouriteMovies.remove(movieId)
        }
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails = withContext(ioDispatcher) {
        // start both requests concurrently and await their results
        val detailsDeferred = async {
            networkDataSource.getMovieDetails(id = movieId)
        }

        val reviewsDeferred = async {
            networkDataSource.getMovieReviews(id = movieId, page = 1)
        }

        val details = detailsDeferred.await()
        val reviews = reviewsDeferred.await()

        return@withContext MovieDetails(
            summary = MovieSummary(
                id = details.id,
                title = details.title,
                posterUrl = details.backdropUrl,
                isFavourite = favouriteMovies.contains(details.id),
                releaseDate = details.releaseDate,
                ratingOutOf10 = details.rating
            ),
            genres = details.genres.map(NetworkMovieGenre::asExternalModel),
            overview = details.overview,
            directorName = details.credits.crew.find {
                it.job.equals(
                    "director",
                    ignoreCase = true
                )
            }?.name ?: "",
            castNames = details.credits.cast.map { it.name },
            reviews = reviews.map(NetworkMovieReview::asExternalModel),
        )
    }
}