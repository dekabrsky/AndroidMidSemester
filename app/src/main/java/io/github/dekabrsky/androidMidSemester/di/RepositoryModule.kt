package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchesCache() }
    single { MatchRepositoryImpl(get(), get()) } bind MatchRepository::class
    single<MatchRepository> { EspnMatchRepository(get(), get()) }

}
