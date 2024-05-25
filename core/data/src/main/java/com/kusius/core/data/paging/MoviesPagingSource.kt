package com.kusius.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kusius.movies.core.model.MovieSummary
import com.kusius.movies.core.network.MovieNetworkDataSource
import com.kusius.movies.core.network.model.NetworkMovie
import com.kusius.movies.core.network.model.asExternalModel

class MoviesPagingSource(private val networkDataSource: MovieNetworkDataSource):
    PagingSource<Int, MovieSummary>() {
    override fun getRefreshKey(state: PagingState<Int, MovieSummary>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieSummary> {
        return try {
            // load page 1 if undefined
            val nextPageNumber = params.key ?: 1
            val response = networkDataSource.getPopularMovies(nextPageNumber)
            LoadResult.Page(
                data = response.map(NetworkMovie::asExternalModel),
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}