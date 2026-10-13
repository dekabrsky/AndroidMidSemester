package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.MatchDataConfig
import io.github.dekabrsky.androidMidSemester.data.MatchDataProvider
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepository
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamSelectionRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchesCache() }
    single<TeamRepository> { TeamRepositoryImpl(get()) }
    single<TeamSelectionRepository> { TeamSelectionRepositoryImpl() }
    single<MatchRepository> {
        when (MatchDataConfig.provider) {
            MatchDataProvider.ESPN -> EspnMatchRepository(get(), get())
            MatchDataProvider.SSTATS -> MatchRepositoryImpl(get(), get(), get())
        }
    }
}
