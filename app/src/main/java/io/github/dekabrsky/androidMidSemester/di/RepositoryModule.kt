package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.source.EspnMatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.SStatsMatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.dsl.module

private const val ESPN_LEAGUE = "all"

val repositoryModule = module {
    single { SStatsMatchesRemoteDataSource(get()) }
    single { EspnMatchesRemoteDataSource(api = get(), league = ESPN_LEAGUE) }
    single<MatchesRemoteDataSource> { get<EspnMatchesRemoteDataSource>() }
    single { MatchesMemoryCache() }
    single<MatchRepository> { MatchRepositoryImpl(get(), get()) }
}
