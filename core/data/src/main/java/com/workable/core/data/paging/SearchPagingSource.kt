package com.workable.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.core.network.model.NetworkMovie
import com.workable.movierama.core.network.model.asExternalModel

data class SearchPagingKey(
    val query: String,
    val page: Int
)

class SearchPagingSource(private val query: String, private val networkDataSource: MovieNetworkDataSource) :
    PagingSource<SearchPagingKey, MovieSummary>() {
    override fun getRefreshKey(state: PagingState<SearchPagingKey, MovieSummary>): SearchPagingKey? = null

    override suspend fun load(params: LoadParams<SearchPagingKey>): LoadResult<SearchPagingKey, MovieSummary> {
        return try {
            // load page 1 if undefined
            val nextPageKey = params.key ?: SearchPagingKey(query = query, page = 1)
            val response =
                networkDataSource.searchMovies(query = nextPageKey.query, page = nextPageKey.page)
            LoadResult.Page(
                data = response.map(NetworkMovie::asExternalModel),
                prevKey = null,
                nextKey = if (response.isEmpty()) null else
                    nextPageKey.copy(page = nextPageKey.page + 1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}