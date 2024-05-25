package com.kusius.movies.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.kusius.feature.details.navigation.movieDetailsScreen
import com.kusius.feature.details.navigation.navigateToPopularMovies
import com.kusius.movies.feature.popular.navigation.popularMoviesNavigationRoute
import com.kusius.movies.feature.popular.navigation.popularMoviesScreen
import com.kusius.movies.ui.MovieRamaAppState

@Composable
fun MovieRamaNavHost(
    appState: MovieRamaAppState,
    modifier: Modifier = Modifier,
    startDestination: String = popularMoviesNavigationRoute
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        popularMoviesScreen(onMovieClick = { movieId ->
            navController.navigateToPopularMovies(movieIdArg = movieId)
        })

        movieDetailsScreen(
            navController = appState.navController
        )
    }
}