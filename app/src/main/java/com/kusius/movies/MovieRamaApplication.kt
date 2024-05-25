package com.kusius.movies

import android.app.Application
import com.kusius.core.data.di.repositoryModule
import com.kusius.feature.details.di.movieDetailsViewmodelModule
import com.kusius.movies.feature.popular.di.popularViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MovieRamaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MovieRamaApplication)
            modules(
                repositoryModule,
                popularViewModelModule,
                movieDetailsViewmodelModule
            )
        }
    }
}