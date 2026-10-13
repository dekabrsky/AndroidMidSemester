package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchMemoryCache
import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.SStatsMatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchMemoryCache() }

    single<MatchRepository>(named("sstats")) {
        SStatsMatchRepositoryImpl(get(), get())
    }

    single<MatchRepository>(named("espn")) {
        EspnMatchRepositoryImpl(get(), get())
    }

    // Задание 4: Бизнес вернулся на SStats
    single<MatchRepository> {
        get<MatchRepository>(named("sstats"))
    }

    // Репозиторий команд
    single<TeamRepository> {
        TeamRepositoryImpl(get())
    }
}
