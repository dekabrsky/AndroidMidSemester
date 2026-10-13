package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
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
    private val teamRepository: TeamRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()
    private val _selectedTeamIds = MutableStateFlow(teamRepository.selectedTeamIds())
    val selectedTeamIds: StateFlow<Set<Long>> = _selectedTeamIds.asStateFlow()
    private var unfilteredMatches: List<Match> = emptyList()

    init {
        load(LocalDate.now())
    }

    fun previousDay() {
        val current = (state.value as? MatchesUiState.Content)?.date ?: LocalDate.now()
        load(current.minusDays(1))
    }

    fun nextDay() {
        val current = (state.value as? MatchesUiState.Content)?.date ?: LocalDate.now()
        load(current.plusDays(1))
    }

    fun saveSelectedTeams(ids: Set<Long>) {
        teamRepository.saveSelectedTeamIds(ids)
        applySelection(ids)
    }

    fun refreshSelectedTeams() {
        applySelection(teamRepository.selectedTeamIds())
    }

    private fun applySelection(ids: Set<Long>) {
        _selectedTeamIds.value = ids
        val current = _state.value
        if (current is MatchesUiState.Content) _state.value = current.copy(matches = filterMatches(unfilteredMatches, ids))
    }

    private fun load(date: LocalDate) {
        _state.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches ->
                    unfilteredMatches = matches
                    _state.value = MatchesUiState.Content(date, filterMatches(matches, _selectedTeamIds.value))
                }
                .onFailure { e ->
                    _state.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }

    private fun filterMatches(matches: List<Match>, ids: Set<Long>): List<Match> =
        if (ids.isEmpty()) matches else matches.filter { it.home.id in ids || it.away.id in ids }
}
