package com.workable.movierama.core.network.ktor

import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.core.network.model.ApiResourceList
import com.workable.movierama.core.network.model.NetworkMovie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val MOVIE_BACKEND_URL = "https://api.themoviedb.org/3/movie/"
private const val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzODVkYTY5YTk1ZTg0OGU4OWRmN2U0MmFjMWNkZWNiMSIsInN1YiI6IjY0ZDdhZmMzZjE0ZGFkMDBhZDRmMTNiOSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.PQMBcGPq76LHOC-6Y3r0ajDC0r-LpL_5ClPiNsvVNEc"
private const val API_KEY = "385da69a95e848e89df7e42ac1cdecb1"

class KtorMovieNetworkDataSource : MovieNetworkDataSource {
    private val client = HttpClient(CIO) {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(ACCESS_TOKEN, API_KEY)
                }
                sendWithoutRequest { request ->
                    request.url.host == MOVIE_BACKEND_URL
                }
            }
        }
        install(ContentNegotiation) {
            json (
               Json {
                   ignoreUnknownKeys = true
                   isLenient = true
                   prettyPrint = true
               }
            )
        }

        install(Resources)
        defaultRequest {
            url(MOVIE_BACKEND_URL)
        }
    }

    private suspend fun popularMoviesPage(page: Int): ApiResourceList<NetworkMovie> {
        val result = client.get(NetworkPopularResource(page = page))
        return result.body()
    }

    override suspend fun getPopularMovies(page: Int): List<NetworkMovie> {
        return popularMoviesPage(page = page).results
    }
}