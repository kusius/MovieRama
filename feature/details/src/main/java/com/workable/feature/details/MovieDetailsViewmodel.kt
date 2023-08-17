package com.workable.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.workable.core.data.repository.MoviesRepository
import com.workable.movierama.core.domain.usecase.FormatDateUseCase
import com.workable.movierama.core.model.MovieDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieDetailsViewmodel(val moviesRepository: MoviesRepository) : ViewModel() {
    val formatDateUseCase = FormatDateUseCase()
    // contains the marked favourite movies. This will be replaced when persistence is added
    // since source of truth will be it and propagate through the repository
    private val viewEventsFlow = MutableStateFlow<List<ViewEvent>>(emptyList())
    private val _movieId = MutableStateFlow<Int>(-1)

    val movieDetails = _movieId.map { movieId ->
        if (movieId >= 0) {
            val movieDetails = moviesRepository.getMovieDetails(movieId)
            val summary = movieDetails.summary
            MovieDetailsUiState.Content(
                movieDetails.copy(
                    summary = summary.copy(releaseDate = formatDateUseCase(summary.releaseDate))
                )
            )
        }
        else
            MovieDetailsUiState.Loading
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MovieDetailsUiState.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val similarMovies = _movieId.flatMapMerge { movieId ->
        Pager(PagingConfig(pageSize = 10)) {
            moviesRepository.getSimilarMoviesPagingSource(movieId)
        }.flow.cachedIn(viewModelScope)
    }

    fun getMovieDetails(movieId: Int) = viewModelScope.launch {
        _movieId.value = movieId
    }

    fun markFavourite(movieId: Int, isFavourite: Boolean) {
        // todo
    }

}

sealed interface MovieDetailsUiState {
    object Loading : MovieDetailsUiState
    data class Content(val movieDetails: MovieDetails) : MovieDetailsUiState
}

sealed interface ViewEvent {
    data class EditFavourite(val movieId: Int, val isFavourite: Boolean): ViewEvent
    object None : ViewEvent
}