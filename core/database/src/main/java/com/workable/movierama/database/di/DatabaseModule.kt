package com.workable.movierama.database.di

import com.workable.movierama.database.MovieRamaDatabase
import com.workable.movierama.database.dao.MovieDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single { MovieRamaDatabase.getInstance(androidApplication()) }

    fun provideMissionDao(db: MovieRamaDatabase) = db.movieDao()
    single {  provideMissionDao(get()) }

    fun provideRemoteKeyDao(db: MovieRamaDatabase) = db.remoteKeyDao()
    single {  provideRemoteKeyDao(get()) }
}