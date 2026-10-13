package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.FilterMatchesUseCase
import io.github.dekabrsky.androidMidSemester.presentation.teams.TeamsViewModel
import io.github.dekabrsky.androidMidSemester.presentation.matches.MatchesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    single { FilterMatchesUseCase() }
    single { GetMatchesUseCase(get()) }
    single { GetMatchUseCase(get()) }
    viewModelOf(::MatchesViewModel)
    single { GetTeamsUseCase(get()) }
    viewModelOf(::TeamsViewModel)
}
