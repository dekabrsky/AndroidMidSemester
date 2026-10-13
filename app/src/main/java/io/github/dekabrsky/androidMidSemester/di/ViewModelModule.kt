package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.domain.usecase.ClearTeamSelectionUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ToggleTeamSelectionUseCase
import io.github.dekabrsky.androidMidSemester.presentation.matches.MatchesViewModel
import io.github.dekabrsky.androidMidSemester.presentation.teams.TeamsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    single { GetMatchesUseCase(get()) }
    single { GetMatchUseCase(get()) }
    single { GetTeamsUseCase(get()) }
    single { ObserveSelectedTeamsUseCase(get()) }
    single { ToggleTeamSelectionUseCase(get()) }
    single { ClearTeamSelectionUseCase(get()) }
    viewModelOf(::MatchesViewModel)
    viewModelOf(::TeamsViewModel)
}
