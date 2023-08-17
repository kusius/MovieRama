package com.workable.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.workable.movierama.core.model.MovieDetails
import com.workable.movierama.core.model.MovieSummary
import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.core.network.model.NetworkMovie
import com.workable.movierama.core.network.model.NetworkMovieDetails
import com.workable.movierama.core.network.model.asExternalModel

data class SimilarMoviesPagingKey(
    val movieId: Int,
    val page: Int
)

class SimilarMoviesPagingSource(
    private val movieId: Int,
    private val networkDataSource: MovieNetworkDataSource
) : PagingSource<SimilarMoviesPagingKey, MovieSummary>() {
    override fun getRefreshKey(state: PagingState<SimilarMoviesPagingKey, MovieSummary>): SimilarMoviesPagingKey? {
       return null
    }

    override suspend fun load(params: LoadParams<SimilarMoviesPagingKey>): LoadResult<SimilarMoviesPagingKey, MovieSummary> {
        return try {
            // load page 1 if undefined
            val nextPageKey = params.key ?: SimilarMoviesPagingKey(movieId = movieId, page = 1)
            val response =
                networkDataSource.getMovieSimilar(id = nextPageKey.movieId, page = nextPageKey.page)
            LoadResult.Page(
                data = response.map(NetworkMovie::asExternalModel),
                prevKey = null,
                nextKey = nextPageKey.copy(page = nextPageKey.page + 1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}