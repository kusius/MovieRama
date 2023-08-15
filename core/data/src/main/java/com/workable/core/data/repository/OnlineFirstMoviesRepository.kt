package com.workable.core.data.repository

import androidx.paging.PagingSource
import com.workable.core.data.paging.MoviesPagingSource
import com.workable.core.data.paging.SearchPagingKey
import com.workable.core.data.paging.SearchPagingSource
import com.workable.movierama.core.model.Movie
import com.workable.movierama.core.network.MovieNetworkDataSource

class OnlineFirstMoviesRepository(private val networkDataSource: MovieNetworkDataSource) : MoviesRepository {
    private val favouriteMovies = mutableSetOf<Int>()
    override fun getPopularMoviesPagingSource(): PagingSource<Int, Movie> {
        return MoviesPagingSource(networkDataSource)
    }

    override fun getSearchResults(query: String): PagingSource<SearchPagingKey, Movie> {
        return SearchPagingSource(query, networkDataSource)
    }

    override suspend fun markFavourite(movieId: Int, isFavourite: Boolean) {
        if (isFavourite) {
            favouriteMovies.add(movieId)
        } else {
            favouriteMovies.remove(movieId)
        }
    }
}