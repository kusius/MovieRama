package com.kusius.core.data.di

import com.kusius.core.data.paging.MoviesRemoteMediator
import com.kusius.core.data.repository.MoviesRepository
import com.kusius.core.data.repository.OfflineFirstMoviesRepository
import com.kusius.core.data.repository.PagingMoviesRepository
import com.kusius.core.data.repository.OnlineFirstMoviesRepository
import com.kusius.movies.core.network.di.networkModule
import com.kusius.movies.database.di.databaseModule
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    includes(databaseModule, networkModule, coroutineDispatcherModule)
    // Will match both at the interface level and implementation when asked to provide dependency
    single {
        OnlineFirstMoviesRepository(
            networkDataSource = get(),
            ioDispatcher = get(named(IO_DISPATCHER))
        )
    } bind PagingMoviesRepository::class

    single {
        OfflineFirstMoviesRepository(
            networkDataSource = get(),
            movieDao = get(),
            ioDispatcher = get(named(IO_DISPATCHER)),
            database = get()
        )
    } bind MoviesRepository::class
}