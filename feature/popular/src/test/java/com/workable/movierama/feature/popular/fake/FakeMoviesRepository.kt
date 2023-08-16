package com.workable.movierama.feature.popular.fake

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.workable.core.data.repository.MoviesRepository
import com.workable.movierama.core.model.MovieSummary

class FakeMoviesRepository(private val testData: List<MovieSummary>): MoviesRepository {
    override fun getPopularMoviesPagingSource(): PagingSource<Int, MovieSummary> {
        return FakeMoviesPagingSource(testData)
    }

    override suspend fun markFavourite(movieId: Int, isFavourite: Boolean) {
        TODO("Not yet implemented")
    }
}

class FakeMoviesPagingSource(private val testData: List<MovieSummary>) : PagingSource<Int, MovieSummary>() {
    override fun getRefreshKey(state: PagingState<Int, MovieSummary>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieSummary> {
        return LoadResult.Page(
            data = testData,
            prevKey = null,
            nextKey = null,
        )
    }

}