package com.kusius.core.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.kusius.core.data.model.asEntity
import com.kusius.movies.core.network.MovieNetworkDataSource
import com.kusius.movies.database.MovieRamaDatabase
import com.kusius.movies.database.model.MovieEntity
import com.kusius.movies.database.model.RemoteKeyEntity

private const val REMOTE_KEY_LABEL = "MoviesRemoteKey"
@OptIn(ExperimentalPagingApi::class)
class MoviesRemoteMediator(
    private val query: String,
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

            val networkResponse =
                if (query.isEmpty() || query.isBlank()) network.getPopularMovies(loadKey)
                else network.searchMovies(query, loadKey)

            val entities = networkResponse.map { networkMovie ->
                val storedMovie = movieDao.getMovieById(networkMovie.id)
                val isFavourite = storedMovie?.isFavourite ?: false
                networkMovie.asEntity(isFavourite = isFavourite, isSearchResult = false)
            }

            // store received data into database
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
//                    movieDao.deleteAll()
                    remoteKeyDao.deleteByQuery(REMOTE_KEY_LABEL)
                }
                // Update RemoteKey for this query.

                remoteKeyDao.insertOrReplace(
                    RemoteKeyEntity(
                        REMOTE_KEY_LABEL, nextKey = if(networkResponse.isEmpty()) null else loadKey + 1
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