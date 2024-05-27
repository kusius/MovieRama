package com.kusius.feature.details

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kusius.core.data.repository.MoviesRepository
import com.kusius.movies.core.domain.usecase.FormatDateUseCase
import com.kusius.movies.core.model.MovieDetails
import com.kusius.movies.feature.details.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

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
    }
        .catch {
            if(it is UnresolvedAddressException)
                emit(MovieDetailsUiState.Error(R.string.error_data_fetch))
        }
        .stateIn(
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

    companion object {
        private const val TAG = "MovieDetailsViewmodel"
    }
}

sealed class MovieDetailsUiState {
    object Loading : MovieDetailsUiState()
    data class Content(val movieDetails: MovieDetails) : MovieDetailsUiState()
    data class Error(@StringRes val message: Int) : MovieDetailsUiState()
}