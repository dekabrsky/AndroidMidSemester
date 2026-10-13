
package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
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

    data class Content(
        val date: LocalDate,
        val matches: List<Match>
    ) : MatchesUiState

    data class Error(val message: String) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
    private val teamSelection: TeamSelectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(
        MatchesUiState.Loading
    )
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    private var currentDate = LocalDate.now()
    private var allMatches: List<Match> = emptyList()
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            teamSelection.selectedTeamIds.collect { ids ->
                val current = _state.value

                if (current is MatchesUiState.Content) {
                    _state.value = current.copy(
                        matches = filterMatches(allMatches, ids)
                    )
                }
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

    private fun filterMatches(
        matches: List<Match>,
        selectedIds: Set<Long>
    ): List<Match> {
        if (selectedIds.isEmpty()) return matches

        return matches.filter { match ->
            match.home.id in selectedIds ||
                    match.away.id in selectedIds
        }
    }

    private fun load(date: LocalDate) {
        currentDate = date
        loadJob?.cancel()
        _state.value = MatchesUiState.Loading

        loadJob = viewModelScope.launch {
            try {
                val matches = getMatches(date)
                allMatches = matches

                _state.value = MatchesUiState.Content(
                    date = date,
                    matches = filterMatches(
                        matches,
                        teamSelection.selectedTeamIds.value
                    )
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = MatchesUiState.Error(
                    e.message ?: "Не удалось загрузить матчи"
                )
            }
        }
    }
}
