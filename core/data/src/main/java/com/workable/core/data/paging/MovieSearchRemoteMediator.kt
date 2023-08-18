package com.workable.core.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.workable.core.data.model.asEntity
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.database.MovieRamaDatabase
import com.workable.movierama.database.dao.MovieDao
import com.workable.movierama.database.model.MovieEntity
import com.workable.movierama.database.model.RemoteKeyEntity

private const val REMOTE_KEY_LABEL = "SearchRemoteKey"
@OptIn(ExperimentalPagingApi::class)
class MovieSearchRemoteMediator (
    private val query: String,
    private val database: MovieRamaDatabase,
    private val network: MovieNetworkDataSource
) : RemoteMediator<Int, MovieEntity>() {
    private val movieDao = database.movieDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        movieDao.clearSearchResult()
        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType) {
                LoadType.REFRESH -> {
                    remoteKeyDao.remoteKeyByQuery(REMOTE_KEY_LABEL)
                    movieDao.clearSearchResult()
                    // load first page
                    1
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = database.withTransaction {
                        remoteKeyDao.remoteKeyByQuery(REMOTE_KEY_LABEL)
                    }

                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    remoteKey.nextKey!!
                }
            }

            val networkResponse= network.searchMovies(
                query = query,
                page = loadKey
            )

            val entities = networkResponse.map { networkMovie ->
                val storedMovie = movieDao.getMovieById(networkMovie.id)
                val isFavourite = storedMovie?.isFavourite ?: false
                networkMovie.asEntity(isFavourite = isFavourite, isSearchResult = true)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDao.deleteAll()
                    remoteKeyDao.deleteByQuery(REMOTE_KEY_LABEL)
                }
                // Update RemoteKey for this query.
                remoteKeyDao.insertOrReplace(
                    RemoteKeyEntity(
                        REMOTE_KEY_LABEL, nextKey = loadKey + 1
                    )
                )
                movieDao.insertAll(entities)
            }

            // we do not store searched movies in the database
            MediatorResult.Success(endOfPaginationReached = networkResponse.isEmpty())

        } catch (e: Exception) {
            Log.e("MovieSearchRemoteMediator", "error: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}