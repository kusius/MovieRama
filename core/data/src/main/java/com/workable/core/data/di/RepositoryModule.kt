package com.workable.core.data.di

import com.workable.core.data.paging.MoviesRemoteMediator
import com.workable.core.data.repository.MoviesRepository
import com.workable.core.data.repository.OfflineFirstMoviesRepository
import com.workable.core.data.repository.PagingMoviesRepository
import com.workable.core.data.repository.OnlineFirstMoviesRepository
import com.workable.movierama.core.network.di.networkModule
import com.workable.movierama.database.di.databaseModule
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
            moviesRemoteMediator = MoviesRemoteMediator(database = get(), network = get(), query = ""),
            ioDispatcher = get(named(IO_DISPATCHER)),
            database = get()
        )
    } bind MoviesRepository::class
}