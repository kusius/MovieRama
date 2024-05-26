package com.kusius.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kusius.core.data.paging.MoviesRemoteMediator
import com.kusius.core.data.paging.SimilarMoviesPagingSource
import com.kusius.movies.core.model.MovieDetails
import com.kusius.movies.core.model.MovieSummary
import com.kusius.movies.core.network.MovieNetworkDataSource
import com.kusius.movies.core.network.model.NetworkMovieGenre
import com.kusius.movies.core.network.model.NetworkMovieReview
import com.kusius.movies.core.network.model.asExternalModel
import com.kusius.movies.database.MovieRamaDatabase
import com.kusius.movies.database.dao.MovieDao
import com.kusius.movies.database.model.MovieEntity
import com.kusius.movies.database.model.asExternalModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
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
        }.flow.map {
            it.map(MovieEntity::asExternalModel)
        }
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