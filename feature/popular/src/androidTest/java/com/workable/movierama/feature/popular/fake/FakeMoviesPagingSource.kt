package com.workable.movierama.feature.popular.fake

import androidx.paging.CombinedLoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.feature.popular.testMovieSummary
import kotlinx.coroutines.delay

/**
 * This paging source loads data after a very long delay. Useful to test UI
 * response to loading namely in compose when the state is [CombinedLoadStates.refresh]
 */
class LoadMoviesPagingSource: PagingSource<Int, MovieSummary>() {
    override fun getRefreshKey(state: PagingState<Int, MovieSummary>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieSummary> {
        delay(Long.MAX_VALUE)
        return LoadResult.Page(data = listOf(testMovieSummary), prevKey = null, nextKey = null)
    }
}

fun createLoadingPagingDataFlow() = Pager(PagingConfig(pageSize = 10)) {
    LoadMoviesPagingSource()
}.flow