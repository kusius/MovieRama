package com.workable.movierama

import android.app.Application
import com.workable.core.data.di.repositoryModule
import com.workable.feature.details.di.movieDetailsViewmodelModule
import com.workable.movierama.feature.popular.di.popularViewModelModule
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