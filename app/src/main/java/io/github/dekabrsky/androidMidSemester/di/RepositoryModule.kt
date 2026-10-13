package io.github.dekabrsky.androidMidSemester.di


import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MatchRepository> { MatchRepositoryImpl(get()) }

}