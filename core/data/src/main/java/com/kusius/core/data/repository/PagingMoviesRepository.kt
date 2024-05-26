package com.kusius.core.data.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.kusius.core.data.paging.SearchPagingKey
import com.kusius.movies.core.model.MovieDetails
import com.kusius.movies.core.model.MovieSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PagingMoviesRepository {
    fun getPopularMoviesPagingSource() : PagingSource<Int, MovieSummary>
    fun getSearchResultsPagingSource(query: String) : PagingSource<SearchPagingKey, MovieSummary>
    suspend fun markFavourite(movieId: Int, isFavourite: Boolean)

    suspend fun getMovieDetails(movieId: Int): MovieDetails

    fun getSimilarMoviesPagingSource(movieId: Int) : PagingSource<Int, MovieSummary>
}


interface MoviesRepository {
    fun getMoviesByQuery(query: String): Flow<PagingData<MovieSummary>>

    fun getSimilarMoviesPagingSource(movieId: Int) : Flow<PagingData<MovieSummary>>
    suspend fun markFavourite(movieId: Int, isFavourite: Boolean)

    fun getMovieDetailsFlow(movieId: Int): Flow<MovieDetails>
}
