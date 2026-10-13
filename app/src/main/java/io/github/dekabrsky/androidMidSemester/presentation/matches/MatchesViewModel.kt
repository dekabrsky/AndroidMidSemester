package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MatchesUiState {
    data object Loading : MatchesUiState
    data class Content(val date: LocalDate, val matches: List<Match>) : MatchesUiState
    data class Error(val message: String) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
    observeSelectedTeams: ObserveSelectedTeamsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    val selectedTeamIds: StateFlow<Set<Long>> = observeSelectedTeams()

    private var currentDate: LocalDate = LocalDate.now()
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            selectedTeamIds.collect { load(currentDate) }
        }
    }

    fun previousDay() = load(currentDate.minusDays(1))

    fun nextDay() = load(currentDate.plusDays(1))

    fun retry() = load(currentDate)

    private fun load(date: LocalDate) {
        currentDate = date
        val teamIds = selectedTeamIds.value
        loadJob?.cancel()
        _state.value = MatchesUiState.Loading
        loadJob = viewModelScope.launch {
            runCatching { getMatches(date, teamIds) }
                .onSuccess { matches -> _state.value = MatchesUiState.Content(date, matches) }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    _state.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }
}
