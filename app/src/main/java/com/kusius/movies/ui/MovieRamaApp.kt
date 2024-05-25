package com.kusius.movies.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.kusius.movies.core.designsystem.theme.MovieRamaTheme
import com.kusius.movies.core.designsystem.theme.component.MovieRamaNavigationBar
import com.kusius.movies.navigation.MovieRamaNavHost
import com.kusius.movies.navigation.TopLevelDestination


@Composable
fun MovieRamaApp(appState: MovieRamaAppState = rememberMovieRamaAppState()) {
    Scaffold() { paddingValues ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(Modifier.fillMaxSize()) {
                val destination= appState.currentTopLevelDestination
                // todo: set top app bar title etc

                MovieRamaNavHost(appState = appState)
            }
        }
    }
}

@Composable
fun MovieRamaBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    MovieRamaNavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestination(destination)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    if (selected) destination.selectedIcon else destination.unselectedIcon
                },

            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val selected = true
    val destination = TopLevelDestination.POPULAR
    MovieRamaTheme {
        MovieRamaApp()
    }
}

private fun NavDestination?.isTopLevelDestination(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false
