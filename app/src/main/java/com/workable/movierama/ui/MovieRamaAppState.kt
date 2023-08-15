package com.workable.movierama.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.workable.movierama.feature.popular.navigation.navigateToPopularMovies
import com.workable.movierama.feature.popular.navigation.popularMoviesNavigationRoute
import com.workable.movierama.navigation.TopLevelDestination

class MovieRamaAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
            @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when(currentDestination?.route) {
            popularMoviesNavigationRoute -> TopLevelDestination.POPULAR
            else -> null
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.POPULAR -> navController.navigateToPopularMovies(topLevelNavOptions)
        }
    }
}

@Composable
fun rememberMovieRamaAppState(navController: NavHostController = rememberNavController()): MovieRamaAppState{
    return remember(
        navController
    ) {
        MovieRamaAppState(navController = navController)
    }
}