package com.workable.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.core.network.model.asExternalModel
import com.workable.movierama.database.dao.MovieDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PersistentSearchPagingSource(private val query: String, private val networkDataSource: MovieNetworkDataSource) :
    PagingSource<SearchPagingKey, MovieSummary>(), KoinComponent {
    private val movieDao: MovieDao by inject()
    override fun getRefreshKey(state: PagingState<SearchPagingKey, MovieSummary>): SearchPagingKey? = null

    override suspend fun load(params: LoadParams<SearchPagingKey>): LoadResult<SearchPagingKey, MovieSummary> {
        return try {
            // load page 1 if undefined
            val nextPageKey = params.key ?: SearchPagingKey(query = query, page = 1)
            val response =
                networkDataSource.searchMovies(query = nextPageKey.query, page = nextPageKey.page)
            val mappedResponses = response.map {  networkMovie ->
                val isFavourite = movieDao.getMovieById(networkMovie.id)?.isFavourite ?: false
                networkMovie.asExternalModel().copy(isFavourite = isFavourite)
            }
            LoadResult.Page(
                data = mappedResponses,
                prevKey = null,
                nextKey = nextPageKey.copy(page = nextPageKey.page + 1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}