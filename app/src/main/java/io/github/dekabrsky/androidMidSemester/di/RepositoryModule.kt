package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { MatchesMemoryCache() }
    single<TeamRepository> { TeamRepositoryImpl(api = get()) }

    single<MatchRepository> {
        MatchRepositoryImpl(
            api = get(),
            cache = get(),
        )
    }
}
