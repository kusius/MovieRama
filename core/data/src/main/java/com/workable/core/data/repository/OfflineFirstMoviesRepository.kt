package com.workable.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.workable.core.data.paging.MoviesRemoteMediator
import com.workable.core.data.paging.SimilarMoviesPagingSource
import com.workable.movierama.core.model.MovieDetails
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.core.network.model.NetworkMovieGenre
import com.workable.movierama.core.network.model.NetworkMovieReview
import com.workable.movierama.core.network.model.asExternalModel
import com.workable.movierama.database.MovieRamaDatabase
import com.workable.movierama.database.dao.MovieDao
import com.workable.movierama.database.model.MovieEntity
import com.workable.movierama.database.model.asExternalModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent


const val PAGE_SIZE = 50
class OfflineFirstMoviesRepository(
    private val networkDataSource: MovieNetworkDataSource,
    private val database: MovieRamaDatabase,
    private val movieDao: MovieDao,
    private val moviesRemoteMediator: MoviesRemoteMediator,
    private val ioDispatcher: CoroutineDispatcher
) : MoviesRepository, KoinComponent {
    @OptIn(ExperimentalPagingApi::class)
    override fun getMoviesByQuery(query: String): Flow<PagingData<MovieSummary>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = MoviesRemoteMediator(query, database, networkDataSource)
        ) {
            movieDao.pagingSourceByQuery(query)
        }.flow.map { it.map(MovieEntity::asExternalModel) }
    }

    override fun getSimilarMoviesPagingSource(movieId: Int): Flow<PagingData<MovieSummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 50
            )
        ) {
            SimilarMoviesPagingSource(movieId = movieId, networkDataSource = networkDataSource)
        }.flow
    }

    override suspend fun markFavourite(movieId: Int, isFavourite: Boolean) = withContext(ioDispatcher) {
        movieDao.updateMovieFavourite(movieId = movieId, isFavourite = isFavourite)
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails = withContext(ioDispatcher){
        val detailsDeferred = async {
            networkDataSource.getMovieDetails(id = movieId)
        }

        val reviewsDeferred = async {
            networkDataSource.getMovieReviews(id = movieId, page = 1)
        }

        val isFavourite = movieDao.getMovieById(movieId)?.isFavourite ?: false

        val details = detailsDeferred.await()
        val reviews = reviewsDeferred.await()

        return@withContext MovieDetails(
            summary = MovieSummary(
                id = details.id,
                title = details.title,
                posterUrl = details.backdropUrl,
                isFavourite = isFavourite,
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