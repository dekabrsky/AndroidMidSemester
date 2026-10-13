package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchCache() }
    single<MatchRepository> { MatchRepositoryImpl(get(), get()) }
    single<TeamRepository> { TeamRepositoryImpl(get()) }
}
