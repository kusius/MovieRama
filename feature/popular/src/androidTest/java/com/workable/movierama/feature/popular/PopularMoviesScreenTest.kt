package com.workable.movierama.feature.popular

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.platform.app.InstrumentationRegistry
import com.workable.movierama.feature.popular.fake.createLoadingPagingDataFlow
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PopularMoviesScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun use_app_context() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.workable.movierama.feature.popular.test", appContext.packageName)
    }

    @Test
    fun movie_screen_shows_loading() {
         val pagingDataFlow = createLoadingPagingDataFlow()

        composeTestRule.setContent {
            MaterialTheme {
                MoviesScreen(
                    onMovieClick = {},
                    onFavouriteChanged = {_, _ -> } ,
                    lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems(),
                    onSearchQueryChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("PullRefreshIndicator").assertIsDisplayed()
    }
}