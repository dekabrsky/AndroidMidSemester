package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.InMemoryTeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.data.repository.SStatsTeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchCache() }
    single<MatchRepository> { MatchRepositoryImpl(get(), get(), teamRepository = get()) }
    single<TeamRepository> { SStatsTeamRepositoryImpl(get()) }
    single<TeamSelectionRepository> { InMemoryTeamSelectionRepository() }
}
