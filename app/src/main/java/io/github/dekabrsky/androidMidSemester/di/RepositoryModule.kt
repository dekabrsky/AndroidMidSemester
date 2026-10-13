package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchesMemoryCache() }
    single<MatchRepository> { EspnMatchRepository(get(), get()) }
}
