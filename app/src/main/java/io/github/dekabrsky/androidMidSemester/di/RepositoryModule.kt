package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.prefs.SelectedTeamsStore
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.source.CachingMatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.EspnRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.SStatsRemoteDataSource
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val USE_ESPN = false

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

    single<TeamRepository> { TeamRepositoryImpl(get()) }

    single { GetTeamsUseCase(get()) }

    single { SelectedTeamsStore(androidContext()) }
}