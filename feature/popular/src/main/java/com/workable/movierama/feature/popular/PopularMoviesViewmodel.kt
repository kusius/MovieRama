package com.workable.movierama.feature.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.workable.core.data.repository.MoviesRepository
import com.workable.movierama.core.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PopularMoviesViewmodel(private val moviesRepository: MoviesRepository) : ViewModel() {
    private val _uiState = Pager(PagingConfig(pageSize = 10)) {
        moviesRepository.getPopularMoviesPagingSource()
    }.flow.cachedIn(viewModelScope)
    val uiState = _uiState

}

sealed interface MovieUiState {
    data class Content(
        val movies : List<Movie>
    ) : MovieUiState

    object Loading : MovieUiState
}

val testMovie = Movie(
    title = "Movie Title",
    posterUrl = "https://placehold.co/600x400",
    isFavourite = true,
    releaseDate = "11/11/11",
    rating = 4.4f
)
