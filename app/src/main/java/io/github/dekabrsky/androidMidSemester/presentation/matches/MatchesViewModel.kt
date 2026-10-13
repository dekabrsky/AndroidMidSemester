package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
    private var currentDate = LocalDate.now()
    private var unfilteredMatches: List<Match> = emptyList()

    init {
        viewModelScope.launch {
            teamRepository.selectedTeamIds.collect { selectedIds ->
                applySelection(selectedIds)
            }
        }
        load(currentDate)
    }

    fun previousDay() {
        load(currentDate.minusDays(1))
    }

    fun nextDay() {
        load(currentDate.plusDays(1))
    }

    private fun load(date: LocalDate) {
        currentDate = date
        _state.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches ->
                    unfilteredMatches = matches
                    _state.value = MatchesUiState.Content(
                        date = date,
                        matches = filterMatches(matches, teamRepository.selectedTeamIds.value),
                    )
                }
                .onFailure { e ->
                    _state.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }

    private fun applySelection(selectedIds: Set<Long>) {
        val content = _state.value as? MatchesUiState.Content ?: return
        _state.value = content.copy(matches = filterMatches(unfilteredMatches, selectedIds))
    }

    private fun filterMatches(matches: List<Match>, selectedIds: Set<Long>): List<Match> =
        if (selectedIds.isEmpty()) {
            matches
        } else {
            matches.filter { match -> match.home.id in selectedIds || match.away.id in selectedIds }
        }
}
