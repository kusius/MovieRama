package com.kusius.movies.feature.popular.fake

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kusius.core.data.repository.MoviesRepository
import com.kusius.movies.core.model.MovieDetails
import com.kusius.movies.core.model.MovieSummary
import kotlinx.coroutines.flow.Flow

class FakeMoviesRepository(private val testData: List<MovieSummary>): MoviesRepository {

    override fun getMoviesByQuery(query: String): Flow<PagingData<MovieSummary>> {
        return Pager(config = PagingConfig(pageSize = 10)){
            FakeMoviesPagingSource(testData)
        }.flow
    }

    override fun getSimilarMoviesPagingSource(movieId: Int): Flow<PagingData<MovieSummary>> {
        TODO("Not yet implemented")
    }

    override suspend fun markFavourite(movieId: Int, isFavourite: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
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