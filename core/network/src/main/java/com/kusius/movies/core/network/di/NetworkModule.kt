package com.kusius.movies.core.network.di

import com.kusius.movies.core.network.MovieNetworkDataSource
import com.kusius.movies.core.network.fake.FakeMovieNetworkDataSource
import com.kusius.movies.core.network.ktor.KtorMovieNetworkDataSource
import org.koin.dsl.module

val networkModule = module {
    single<MovieNetworkDataSource> { KtorMovieNetworkDataSource() }
    single { FakeMovieNetworkDataSource() }
}