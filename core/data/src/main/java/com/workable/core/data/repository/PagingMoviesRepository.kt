package com.workable.core.data.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.workable.core.data.paging.SearchPagingKey
import com.workable.core.data.paging.SimilarMoviesPagingKey
import com.workable.movierama.core.model.MovieDetails
import com.workable.movierama.core.model.MovieSummary
import kotlinx.coroutines.flow.Flow

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

    suspend fun getMovieDetails(movieId: Int): MovieDetails
}
