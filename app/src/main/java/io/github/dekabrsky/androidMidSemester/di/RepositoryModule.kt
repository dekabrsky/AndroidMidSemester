package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchCache() }
    single<MatchRepository> { EspnMatchRepositoryImpl(get(), get()) }
}
