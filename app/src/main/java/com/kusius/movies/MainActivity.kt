package com.kusius.movies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kusius.movies.core.designsystem.theme.MovieRamaTheme
import com.kusius.movies.ui.MovieRamaApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieRamaTheme {
                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    MovieRamaApp("Android")
//                }
                MovieRamaApp()
            }
        }
    }
}