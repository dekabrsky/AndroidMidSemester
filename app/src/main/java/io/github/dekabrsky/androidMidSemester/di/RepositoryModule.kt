package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.repository.EspnMatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<MatchRepository>(named("sstats")) {
        MatchRepositoryImpl(get())
    }
    single<MatchRepository> {
        EspnMatchRepositoryImpl(get())
    }
}