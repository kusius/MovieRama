package com.workable.core.data.repository

import androidx.paging.PagingSource
import com.workable.core.data.paging.SearchPagingKey
import com.workable.movierama.core.model.Movie

interface MoviesRepository {
    fun getPopularMoviesPagingSource() : PagingSource<Int, Movie>
    fun getSearchResults(query: String) : PagingSource<SearchPagingKey, Movie>
    suspend fun markFavourite(movieId: Int, isFavourite: Boolean)
}