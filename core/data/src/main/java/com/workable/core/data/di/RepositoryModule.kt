package com.workable.core.data.di

import com.workable.core.data.repository.MoviesRepository
import com.workable.core.data.repository.OnlineFirstMoviesRepository
import com.workable.movierama.core.network.di.networkModule
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    includes(networkModule, coroutineDispatcherModule)
    // Will match both at the interface level and implementation when asked to provide dependency
    single {
        OnlineFirstMoviesRepository(
            networkDataSource = get(),
            ioDispatcher = get(named(IO_DISPATCHER))
        )
    } bind MoviesRepository::class
}