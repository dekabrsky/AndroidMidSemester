package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import io.github.dekabrsky.androidMidSemester.presentation.matches.MatchesViewModel
import io.github.dekabrsky.androidMidSemester.presentation.teamselection.TeamSelectionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    single { GetMatchesUseCase(get()) }
    single { GetMatchUseCase(get()) }
    single { GetTeamsUseCase(get(named("sstats"))) }   // sstats-репозиторий
    viewModelOf(::MatchesViewModel)
    viewModelOf(::TeamSelectionViewModel)
}