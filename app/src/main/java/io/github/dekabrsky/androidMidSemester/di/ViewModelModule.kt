package io.github.dekabrsky.androidMidSemester.di

import io.github.dekabrsky.androidMidSemester.domain.repository.CountriesRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamsRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.FilterMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import io.github.dekabrsky.androidMidSemester.presentation.matches.MatchesViewModel
import io.github.dekabrsky.androidMidSemester.presentation.teams.TeamsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { FilterMatchesUseCase() }
    single { GetMatchesUseCase(get<MatchRepository>()) }
    single { GetMatchUseCase(get<MatchRepository>()) }
    single { GetTeamsUseCase(get<TeamsRepository>()) }

    viewModel {
        MatchesViewModel(
            getMatches = get<GetMatchesUseCase>(),
            filterMatches = get<FilterMatchesUseCase>(),
            selectionRepository = get<TeamSelectionRepository>(),
            countriesRepository = get<CountriesRepository>(),
        )
    }

    viewModel {
        TeamsViewModel(
            getTeams = get<GetTeamsUseCase>(),
            selectionRepository = get<TeamSelectionRepository>(),
            countriesRepository = get<CountriesRepository>(),
        )
    }
}