package com.workable.movierama.feature.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.workable.core.data.repository.MoviesRepository
import com.workable.core.data.repository.PagingMoviesRepository
import com.workable.movierama.core.domain.usecase.FormatDateUseCase
import com.workable.movierama.core.model.MovieSummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PopularMoviesViewmodel(private val moviesRepository: MoviesRepository) : ViewModel() {
    val formatDateUseCase = FormatDateUseCase()
    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState =
        _searchQuery
        // only trigger search query if value not changed within 500ms in order to not overwhelm the
        // repository if user is typing their query
        .debounce(500L)
        // the source of data is either the paged search results, or the popular movies
        .flatMapMerge { query ->
            moviesRepository.getMoviesByQuery(query)
        }
        .map { pagingData ->
            pagingData.map { movie ->
                movie.copy(releaseDate = formatDateUseCase(movie.releaseDate))
            }
        }

    fun markFavourite(movieId: Int, isFavourite: Boolean) {
        viewModelScope.launch {
            moviesRepository.markFavourite(movieId = movieId, isFavourite = isFavourite)
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
        }
    }
}

val testMovieSummary = MovieSummary(
    id = -1,
    title = "Spider-Man:",
    posterUrl = "https://placehold.co/600x400",
    isFavourite = true,
    releaseDate = "6 Jun 1934",
    ratingOutOf10 = 4.4f
)
