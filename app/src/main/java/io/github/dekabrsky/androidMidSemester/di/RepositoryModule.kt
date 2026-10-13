package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.remote.EspnRemoteSource
import io.github.dekabrsky.androidMidSemester.data.remote.RemoteSource
import io.github.dekabrsky.androidMidSemester.data.remote.SStatsRemoteSource
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.MatchesCache
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { SStatsRemoteSource(get()) }
    single { EspnRemoteSource(get()) }

    single<MatchRepository> { MatchRepositoryImpl(get()) }
    single<MatchesCache> { MatchesCache() }

    single<RemoteSource> { get<EspnRemoteSource>() }
}
