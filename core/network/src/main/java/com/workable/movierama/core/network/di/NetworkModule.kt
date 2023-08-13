package com.workable.movierama.core.network.di

import com.workable.movierama.core.network.MovieNetworkDataSource
import com.workable.movierama.core.network.fake.FakeMovieNetworkDataSource
import com.workable.movierama.core.network.ktor.KtorMovieNetworkDataSource
import org.koin.dsl.module

val networkModule = module {
    single<MovieNetworkDataSource> { KtorMovieNetworkDataSource() }
    single { FakeMovieNetworkDataSource() }
}