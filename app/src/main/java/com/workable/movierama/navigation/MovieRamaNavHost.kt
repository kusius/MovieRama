package com.workable.movierama.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.workable.movierama.feature.popular.navigation.popularMoviesNavigationRoute
import com.workable.movierama.feature.popular.navigation.popularMoviesScreen
import com.workable.movierama.ui.MovieRamaAppState

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
        // todo: open details for clicked movie
        popularMoviesScreen(onMovieClick = {})
    }
}