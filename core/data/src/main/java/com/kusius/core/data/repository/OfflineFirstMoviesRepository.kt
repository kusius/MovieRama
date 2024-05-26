package com.kusius.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.kusius.core.data.model.asCrossRef
import com.kusius.core.data.model.asEntity
import com.kusius.core.data.paging.MoviesRemoteMediator
import com.kusius.core.data.paging.SimilarMoviesPagingSource
import com.kusius.movies.core.model.MovieDetails
import com.kusius.movies.core.model.MovieSummary
import com.kusius.movies.core.network.MovieNetworkDataSource
import com.kusius.movies.core.network.model.NetworkMovieDetails
import com.kusius.movies.core.network.model.NetworkMovieGenre
import com.kusius.movies.core.network.model.NetworkMovieReview
import com.kusius.movies.core.network.model.asExternalModel
import com.kusius.movies.database.MovieRamaDatabase
import com.kusius.movies.database.dao.MovieDao
import com.kusius.movies.database.model.GenreEntity
import com.kusius.movies.database.model.MovieEntity
import com.kusius.movies.database.model.ReviewEntity
import com.kusius.movies.database.model.asExternalModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent


const val PAGE_SIZE = 50
class OfflineFirstMoviesRepository(
    private val networkDataSource: MovieNetworkDataSource,
    private val database: MovieRamaDatabase,
    private val movieDao: MovieDao,
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

    override fun getMovieDetailsFlow(movieId: Int): Flow<MovieDetails> = movieDao.getMovieDetailsFlow(movieId).map { details ->
            MovieDetails(
                summary = details.movie.asExternalModel(),
                castNames = details.cast.map { it.name },
                directorName = details.crew.firstOrNull{ it.job.equals(ignoreCase = true, other = "director") }?.name ?: "",
                genres = details.genres.map(GenreEntity::asExternalModel),
                overview = details.movie.overview,
                reviews = details.reviews.map(ReviewEntity::asExternalModel)
            )
    }.onEach {
        withContext(ioDispatcher) {
            if(it.genres.isEmpty() || it.castNames.isEmpty() || it.directorName.isBlank() || it.directorName.isEmpty()) {
                launch {
                    val networkDetailsDeferred = async {
                            networkDataSource.getMovieDetails(id = movieId)
                        }
                    val networkReviewsDeferred = async {
                        networkDataSource.getMovieReviews(id = movieId, page = 1)
                    }

                    val results = awaitAll(networkDetailsDeferred, networkReviewsDeferred)
                    val networkMovieDetails = results[0] as NetworkMovieDetails
                    val networkMovieReviews = results[1] as List<NetworkMovieReview>

                    database.withTransaction {
                        movieDao.insertReview(networkMovieReviews.map { it.asEntity(movieId) })
                        movieDao.insertCrew(networkMovieDetails.credits.crew.map { it.asEntity(movieId) })
                        movieDao.insertCast(networkMovieDetails.credits.cast.map { it.asEntity(movieId) })
                        movieDao.updateMovieOverview(movieId, overview = networkMovieDetails.overview)
                        movieDao.insertGenres(networkMovieDetails.genres.map(NetworkMovieGenre::asEntity))
                        movieDao.associateMovieGenres(networkMovieDetails.genres.map {it.asCrossRef(movieId)})
                    }
                }
            }
        }
    }
}