package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchMemoryCache
import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.SStatsMatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchMemoryCache() }

    // Обе реализации доступны в контейнере DI
    single<MatchRepository>(named("sstats")) {
        SStatsMatchRepositoryImpl(get(), get())
    }

    single<MatchRepository>(named("espn")) {
        EspnMatchRepositoryImpl(get(), get())
    }

    // Активная реализация по умолчанию: ESPN (согласно Заданию 3).
    // Для переключения обратно на SStats достаточно поменять `get(named("espn"))` на `get(named("sstats"))`
    single<MatchRepository> {
        get<MatchRepository>(named("espn"))
    }
}
