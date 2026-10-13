package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.repository.InMemoryTeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.SStatsTeamRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchesMemoryCache() }
    single<MatchRepository> { MatchRepositoryImpl(get(), get()) }
    single<TeamRepository> { SStatsTeamRepository(get()) }
    single<TeamSelectionRepository> { InMemoryTeamSelectionRepository() }
}
