package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
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
    teams: TeamRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()
    val selectedTeamIds = teams.selectedTeamIds
    private var currentDate = LocalDate.now()
    private var allMatches = emptyList<Match>()
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
        loadJob?.cancel()
        currentDate = date
        _state.value = MatchesUiState.Loading
        loadJob = viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches ->
                    allMatches = matches
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
        val matches = allMatches.filter { ids.isEmpty() || it.home.id in ids || it.away.id in ids }
        _state.value = MatchesUiState.Content(currentDate, matches)
    }
}
