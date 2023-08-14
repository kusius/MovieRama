package com.workable.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.workable.movierama.core.model.Movie
import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.core.network.model.NetworkMovie
import com.workable.movierama.core.network.model.asExternalModel

class MoviesPagingSource(private val networkDataSource: MovieNetworkDataSource):
    PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
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