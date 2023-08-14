package com.workable.core.data.repository

import androidx.paging.PagingSource
import com.workable.movierama.core.model.Movie

interface MoviesRepository {
    fun getPopularMoviesPagingSource() : PagingSource<Int, Movie>
    suspend fun markFavourite(movieId: Int, isFavourite: Boolean)
}