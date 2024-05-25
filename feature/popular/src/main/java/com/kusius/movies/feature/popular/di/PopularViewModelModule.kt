package com.kusius.movies.feature.popular.di

import com.kusius.movies.feature.popular.PopularMoviesViewmodel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val popularViewModelModule = module {
    viewModel { PopularMoviesViewmodel(moviesRepository = get()) }
}