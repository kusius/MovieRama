package com.workable.feature.details.di

import com.workable.feature.details.MovieDetailsViewmodel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val movieDetailsViewmodelModule = module {
    viewModel { MovieDetailsViewmodel(moviesRepository = get()) }
}