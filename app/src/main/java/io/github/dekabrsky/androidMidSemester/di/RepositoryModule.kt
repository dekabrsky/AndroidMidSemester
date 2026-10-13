package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.SelectedTeamsRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.SelectedTeamsRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchCache() }
    // Бизнес вернулся на sstats: только там есть список команд страны и фильтр матчей по командам.
    // EspnMatchRepositoryImpl остаётся в проекте, но в DI не подключён.
    single<MatchRepository> { MatchRepositoryImpl(get(), get()) }
    single<TeamRepository> { TeamRepositoryImpl(get()) }
    single<SelectedTeamsRepository> { SelectedTeamsRepositoryImpl(androidContext()) }
}
