package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.presentation.matches.MatchesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import io.github.dekabrsky.androidMidSemester.presentation.teams.TeamsViewModel

val viewModelModule = module {
    single { GetMatchesUseCase(get()) }
    single { GetMatchUseCase(get()) }
    viewModelOf(::MatchesViewModel)
    viewModelOf(::TeamsViewModel)
}
