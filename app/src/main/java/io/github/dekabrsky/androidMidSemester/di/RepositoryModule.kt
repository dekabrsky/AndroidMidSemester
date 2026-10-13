package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.MatchCache
import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepository
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.dsl.module
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionStore

val repositoryModule = module {
    single { MatchCache() }

    // Старый поставщик SStats сохранён
    single {
        MatchRepositoryImpl(
            api = get(),
            cache = get(),
        )
    }

    // По умолчанию приложение использует ESPN
    single<MatchRepository> {
        EspnMatchRepository(
            api = get<EspnApi>(),
            cache = get(),
        )
    }

    single { TeamRepository(api = get<SStatsApi>()) }
    single { TeamSelectionStore() }
}