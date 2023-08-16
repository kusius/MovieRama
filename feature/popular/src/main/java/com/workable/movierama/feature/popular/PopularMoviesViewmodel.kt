package com.workable.movierama.feature.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.workable.core.data.repository.MoviesRepository
import com.workable.movierama.core.domain.usecase.FormatDateUseCase
import com.workable.movierama.core.model.MovieSummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PopularMoviesViewmodel(private val moviesRepository: MoviesRepository) : ViewModel() {
    val formatDateUseCase = FormatDateUseCase()
    // contains the marked favourite movies. This will be replaced when persistence is added
    // since source of truth will be it and propagate through the repository
    private val viewEventsFlow = MutableStateFlow<List<ViewEvent>>(emptyList())
    private val popularMovies = Pager(PagingConfig(pageSize = 10)) {
        moviesRepository.getPopularMoviesPagingSource()
    }.flow.cachedIn(viewModelScope)
    private fun searchedMovies(query: String) = Pager(PagingConfig(pageSize = 10)) {
        moviesRepository.getSearchResultsPagingSource(query)
    }.flow.cachedIn(viewModelScope)

    private val _searchQuery = MutableStateFlow<String>("")

//    val test: Flow<PagingData<Movie>> = _searchQuery.transformLatest { query ->
//        if (query.isNullOrEmpty())
//            popularMovies
//        else
//
//    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState = _searchQuery
        // only trigger search query if value not changed within 500ms in order to not overwhelm the
        // repository if user is typing their query
        .debounce(500L)
        // the source of data is either the paged search results, or the popular movies
        .flatMapMerge { query ->
            if (query.isEmpty() || query.isBlank()) popularMovies
            else searchedMovies(query)
        }
        .map { pagingData ->
            pagingData.map { movie ->
                movie.copy(releaseDate = formatDateUseCase(movie.releaseDate))
            }
        }
        .combine(viewEventsFlow) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyEvent(acc, event)
            }
        }


    private fun applyEvent(pagingData: PagingData<MovieSummary>, viewEvent: ViewEvent): PagingData<MovieSummary> {
        return when (viewEvent) {
            ViewEvent.None -> pagingData
            is ViewEvent.EditFavourite -> {
                pagingData.map {
                    if (it.id == viewEvent.movieId) it.copy(isFavourite = viewEvent.isFavourite)
                    else it
                }
            }
        }
    }
    fun markFavourite(movieId: Int, isFavourite: Boolean) {
        viewModelScope.launch {
            // todo: propagate to the repo to update persistence
//            moviesRepository.markFavourite(movieId = movieId, isFavourite = isFavourite)
            viewEventsFlow.value +=
                ViewEvent.EditFavourite(movieId = movieId, isFavourite = isFavourite)
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
        }
    }
}

sealed interface MovieUiState {
    data class Content(
        val movieSummaries : List<MovieSummary>
    ) : MovieUiState

    object Loading : MovieUiState
}

sealed interface ViewEvent {
    data class EditFavourite(val movieId: Int, val isFavourite: Boolean): ViewEvent
    object None : ViewEvent
}

val testMovieSummary = MovieSummary(
    id = -1,
    title = "Movie Title",
    posterUrl = "https://placehold.co/600x400",
    isFavourite = true,
    releaseDate = "6 Jun 1934",
    ratingOutOf10 = 4.4f
)
