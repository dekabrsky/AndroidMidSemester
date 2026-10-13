package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.source.CachingMatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.EspnRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.SStatsRemoteDataSource
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.dsl.module

private const val USE_ESPN = true

val repositoryModule = module {

    single<MatchesRemoteDataSource> {
        val delegate: MatchesRemoteDataSource = if (USE_ESPN) {
            EspnRemoteDataSource(get())
        } else {
            SStatsRemoteDataSource(get())
        }
        CachingMatchesRemoteDataSource(
            delegate = delegate,
            name = if (USE_ESPN) "espn" else "sstats",
            scope = get(ApplicationScope),
        )
    }

    single<MatchRepository> { MatchRepositoryImpl(get()) }
}