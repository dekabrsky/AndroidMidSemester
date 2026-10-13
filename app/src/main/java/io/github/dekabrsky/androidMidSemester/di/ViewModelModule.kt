
package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.presentation.matches.MatchesViewModel
import io.github.dekabrsky.androidMidSemester.presentation.teams.TeamsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    single { GetMatchesUseCase(get()) }
    single { GetMatchUseCase(get()) }

    viewModelOf(::MatchesViewModel)
    viewModelOf(::TeamsViewModel)
}
