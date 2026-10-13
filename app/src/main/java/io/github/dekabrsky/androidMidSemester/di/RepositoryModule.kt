package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.source.EspnRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.SStatsRemoteDataSource
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchesCache() }
    single { SStatsRemoteDataSource(get()) }
    single { EspnRemoteDataSource(get()) }
    single<MatchesRemoteDataSource> { get<SStatsRemoteDataSource>() }
    single<MatchRepository> { MatchRepositoryImpl(get(), get()) }
    single<TeamRepository> { TeamRepositoryImpl(get()) }
}
