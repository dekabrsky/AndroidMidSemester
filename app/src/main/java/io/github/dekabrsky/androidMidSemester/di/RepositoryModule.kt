package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.dsl.module
import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.repository.RepositoryImpl
val repositoryModule = module {
    single { MatchCache() }
    single<MatchRepository> { RepositoryImpl(get(), get()) }
}
