package com.kusius.core.data.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val IO_DISPATCHER = "IODispatcher"
internal val coroutineDispatcherModule = module {
    single(named(IO_DISPATCHER)) {
        Dispatchers.IO
    }
}