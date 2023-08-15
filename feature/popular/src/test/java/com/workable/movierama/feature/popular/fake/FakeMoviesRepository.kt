package com.workable.movierama.feature.popular.fake

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.workable.core.data.repository.MoviesRepository
import com.workable.movierama.core.model.Movie

class FakeMoviesRepository(private val testData: List<Movie>): MoviesRepository {
    override fun getPopularMoviesPagingSource(): PagingSource<Int, Movie> {
        return FakeMoviesPagingSource(testData)
    }

    override suspend fun markFavourite(movieId: Int, isFavourite: Boolean) {
        TODO("Not yet implemented")
    }
}

class FakeMoviesPagingSource(private val testData: List<Movie>) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return LoadResult.Page(
            data = testData,
            prevKey = null,
            nextKey = null,
        )
    }

}