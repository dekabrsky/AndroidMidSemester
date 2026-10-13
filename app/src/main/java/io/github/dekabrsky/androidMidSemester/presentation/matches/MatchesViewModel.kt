package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.CountriesRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.FilterMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.CountriesLoader
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MatchesUiState {
    val date: LocalDate

    data class Loading(override val date: LocalDate) : MatchesUiState
    data class Content(
        override val date: LocalDate,
        val matches: List<Match>,
        val isFilterActive: Boolean = false,
    ) : MatchesUiState
    data class Error(
        override val date: LocalDate,
        val message: String,
        val cause: Throwable,
    ) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
    private val filterMatches: FilterMatchesUseCase,
    private val selectionRepository: TeamSelectionRepository,
    private val countriesRepository: CountriesRepository,
) : ViewModel() {

    private val loadedState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading(LocalDate.now()))
    private val countrySelection = MutableStateFlow<String?>(null)
    val selectedCountry = countrySelection.asStateFlow()

    private val countriesLoader = CountriesLoader(countriesRepository, viewModelScope)
    val countries = countriesLoader.state

    val state: StateFlow<MatchesUiState> = combine(
        loadedState,
        selectionRepository.selectedTeams,
        countrySelection,
    ) { loaded, selected, country ->
        when (loaded) {
            is MatchesUiState.Content -> loaded.copy(
                matches = filterMatches(loaded.matches, selected.keys, country),
                isFilterActive = selected.isNotEmpty() || country != null,
            )
            else -> loaded
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, loadedState.value)

    private var loadVersion = 0L

    init {
        load(state.value.date)
    }

    fun previousDay() {
        load(state.value.date.minusDays(1))
    }

    fun nextDay() {
        load(state.value.date.plusDays(1))
    }

    fun selectCountry(country: String?) {
        if (country != null && country !in countries.value.countries) return
        countrySelection.value = country
    }

    fun loadCountries() {
        countriesLoader.load()
    }

    fun retry() {
        val current = loadedState.value
        if (current is MatchesUiState.Error) load(current.date)
    }

    private fun load(date: LocalDate) {
        val version = ++loadVersion
        loadedState.value = MatchesUiState.Loading(date)
        viewModelScope.launch {
            try {
                val matches = getMatches(date)
                if (version == loadVersion) {
                    loadedState.value = MatchesUiState.Content(date, matches)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (version == loadVersion) {
                    loadedState.value = MatchesUiState.Error(
                        date = date,
                        message = e.message ?: "Не удалось загрузить матчи",
                        cause = e,
                    )
                }
            }
        }
    }
}