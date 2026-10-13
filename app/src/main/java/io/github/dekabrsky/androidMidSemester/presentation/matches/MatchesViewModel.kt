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

    private val selectedTeamIds = observeSelectedTeams()
    private var currentDate = LocalDate.now()
    private var dayMatches: List<Match> = emptyList()
    private var loadJob: Job? = null

    init {
        load(currentDate)
        viewModelScope.launch {
            selectedTeamIds.collect {
                if (_state.value is MatchesUiState.Content) showMatches()
            }
        }
    }

    fun previousDay() {
        load(currentDate.minusDays(1))
    }

    fun nextDay() {
        load(currentDate.plusDays(1))
    }

    private fun load(date: LocalDate) {
        currentDate = date
        loadJob?.cancel()
        _state.value = MatchesUiState.Loading
        loadJob = viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches ->
                    dayMatches = matches
                    showMatches()
                }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    _state.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }

    private fun showMatches() {
        val ids = selectedTeamIds.value
        val matches = if (ids.isEmpty()) {
            dayMatches
        } else {
            dayMatches.filter { it.home.id in ids || it.away.id in ids }
        }
        _state.value = MatchesUiState.Content(currentDate, matches)
    }
}
