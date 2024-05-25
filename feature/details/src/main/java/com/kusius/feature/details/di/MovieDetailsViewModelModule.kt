package com.kusius.feature.details.di

import com.kusius.feature.details.MovieDetailsViewmodel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val movieDetailsViewmodelModule = module {
    viewModel { MovieDetailsViewmodel(moviesRepository = get()) }
}