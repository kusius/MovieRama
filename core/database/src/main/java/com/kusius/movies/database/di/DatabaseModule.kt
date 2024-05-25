package com.kusius.movies.database.di

import com.kusius.movies.database.MovieRamaDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single { MovieRamaDatabase.getInstance(androidApplication()) }

    fun provideMissionDao(db: MovieRamaDatabase) = db.movieDao()
    single {  provideMissionDao(get()) }

    fun provideRemoteKeyDao(db: MovieRamaDatabase) = db.remoteKeyDao()
    single {  provideRemoteKeyDao(get()) }
}