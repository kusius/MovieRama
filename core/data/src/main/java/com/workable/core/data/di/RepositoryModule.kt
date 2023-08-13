package com.workable.core.data.di

import com.workable.core.data.repository.MoviesRepository
import com.workable.core.data.repository.OnlineFirstMoviesRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    // Will match both the interface and implementation when asked to provide dependency
    single { OnlineFirstMoviesRepository(networkDataSource = get()) } bind MoviesRepository::class
}