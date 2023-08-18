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
import com.workable.movierama.core.network.model.NetworkMovie
import com.workable.movierama.database.MovieRamaDatabase
import com.workable.movierama.database.dao.MovieDao
import com.workable.movierama.database.dao.RemoteKeyDao
import com.workable.movierama.database.model.MovieEntity
import com.workable.movierama.database.model.RemoteKeyEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val REMOTE_KEY_LABEL = "MoviesRemoteKey"
@OptIn(ExperimentalPagingApi::class)
class MoviesRemoteMediator(
    private val database: MovieRamaDatabase,
    private val network: MovieNetworkDataSource
) : RemoteMediator<Int, MovieEntity>() {
    private val movieDao = database.movieDao()
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType) {
                // for refresh: passing 1 to load from the beginning (first page)
                LoadType.REFRESH -> {
                    // in place refresh
                    1
                }
                // we will not prepend, since refresh always triggers a load from scratch
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
            val networkResponse = network.getPopularMovies(loadKey)

            val entities = networkResponse.map { networkMovie ->
                val storedMovie = movieDao.getMovieById(networkMovie.id)
                val isFavourite = storedMovie?.isFavourite ?: false
                networkMovie.asEntity(isFavourite = isFavourite, isSearchResult = false)
            }

            // store received data into database
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

            // End of pagination reached if network does not provide any more movies
            MediatorResult.Success(endOfPaginationReached = networkResponse.isEmpty())
        } catch (e: Exception) {
            Log.e("MoviesRemoteMediator", "error: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}