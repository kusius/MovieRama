package com.workable.movierama.feature.popular.di

import com.workable.movierama.feature.popular.PopularMoviesViewmodel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val popularViewModelModule = module {
    viewModel { PopularMoviesViewmodel(moviesRepository = get()) }
}