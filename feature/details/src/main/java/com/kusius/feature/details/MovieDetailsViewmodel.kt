package com.kusius.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kusius.core.data.repository.MoviesRepository
import com.kusius.movies.core.domain.usecase.FormatDateUseCase
import com.kusius.movies.core.model.MovieDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class MovieDetailsViewmodel(val moviesRepository: MoviesRepository) : ViewModel() {
    val formatDateUseCase = FormatDateUseCase()
    private val _movieId = MutableStateFlow<Int>(-1)

    val movieDetails: StateFlow<MovieDetailsUiState> = _movieId.transform { movieId ->
        if (movieId >= 0) {
            val movieDetailsFlow = moviesRepository.getMovieDetailsFlow(movieId)

            emitAll(movieDetailsFlow.map { movieDetails ->
                    val summary = movieDetails.summary
                    MovieDetailsUiState.Content(
                        movieDetails.copy(
                            summary = summary.copy(releaseDate = formatDateUseCase(summary.releaseDate))
                        )
                    )
                }
            )
        } else {
            emit(MovieDetailsUiState.Loading)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MovieDetailsUiState.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val similarMovies = _movieId.flatMapMerge { movieId ->
        moviesRepository.getSimilarMoviesPagingSource(movieId)
    }

    fun getMovieDetails(movieId: Int) = viewModelScope.launch {
        _movieId.value = movieId
    }

    fun markFavourite(movieId: Int, isFavourite: Boolean) = viewModelScope.launch {
        moviesRepository.markFavourite(movieId = movieId, isFavourite = isFavourite)
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