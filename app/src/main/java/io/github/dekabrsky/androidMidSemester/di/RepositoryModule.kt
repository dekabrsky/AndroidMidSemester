package io.github.dekabrsky.androidMidSemester.di

import android.content.Context
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.SelectedTeamsRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.source.EspnMatchesDataSource
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.data.source.SStatsMatchesDataSource
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.SelectedTeamsRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val PREFS_NAME = "football_digest_prefs"

enum class MatchesProvider { SSTATS, ESPN }

val ACTIVE_MATCHES_PROVIDER = MatchesProvider.SSTATS

val repositoryModule = module {
    single { SStatsMatchesDataSource(get()) }
    single { EspnMatchesDataSource(get()) }
    single<MatchesRemoteDataSource> {
        when (ACTIVE_MATCHES_PROVIDER) {
            MatchesProvider.SSTATS -> get<SStatsMatchesDataSource>()
            MatchesProvider.ESPN -> get<EspnMatchesDataSource>()
        }
    }

    single { MatchesMemoryCache() }
    single<MatchRepository> { MatchRepositoryImpl(get(), get()) }

    single<TeamRepository> { TeamRepositoryImpl(get()) }
    single<SelectedTeamsRepository> {
        SelectedTeamsRepositoryImpl(
            androidContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        )
    }
}
