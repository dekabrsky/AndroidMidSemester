package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.repository.SStatsMatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.SStatsTeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TeamRepository> { SStatsTeamRepositoryImpl(get()) }
    single<MatchRepository> { SStatsMatchRepositoryImpl(get()) }
}
