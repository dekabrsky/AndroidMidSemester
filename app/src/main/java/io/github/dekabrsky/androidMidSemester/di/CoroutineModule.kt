package io.github.dekabrsky.androidMidSemester.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ApplicationScope = named("ApplicationScope")

val coroutineModule = module {
    single<CoroutineScope>(ApplicationScope) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}