package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.repository.InMemoryTeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.source.EspnMatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.SStatsMatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import org.koin.dsl.module

private const val ESPN_LEAGUE = "all"

val repositoryModule = module {
    single { SStatsMatchesRemoteDataSource(get()) }
    single { EspnMatchesRemoteDataSource(api = get(), league = ESPN_LEAGUE) }
    single<MatchesRemoteDataSource> { get<SStatsMatchesRemoteDataSource>() }
    single { MatchesCache() }
    single<MatchRepository> { MatchRepositoryImpl(get(), get()) }
    single<TeamRepository> { TeamRepositoryImpl(get()) }
    single<TeamSelectionRepository> { InMemoryTeamSelectionRepository() }
}