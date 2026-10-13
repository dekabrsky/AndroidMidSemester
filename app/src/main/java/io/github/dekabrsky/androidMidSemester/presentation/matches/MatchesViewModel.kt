package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.data.selection.TeamSelectionStore
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
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
    private val selection: TeamSelectionStore,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    /** Полный список матчей за текущую дату — его перефильтровываем при смене выбора команд. */
    private var allMatches: List<Match> = emptyList()

    init {
        load(LocalDate.now())
        viewModelScope.launch {
            selection.selectedTeamIds.collect { applyFilter() }
        }
    }

    fun previousDay() {
        val current = (state.value as? MatchesUiState.Content)?.date ?: LocalDate.now()
        load(current.minusDays(1))
    }

    fun nextDay() {
        val current = (state.value as? MatchesUiState.Content)?.date ?: LocalDate.now()
        load(current.plusDays(1))
    }

    private fun load(date: LocalDate) {
        _state.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches ->
                    allMatches = matches
                    _state.value = MatchesUiState.Content(date, filter(matches))
                }
                .onFailure { e ->
                    _state.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }

    /** Перефильтровывает уже загруженный список, не ходя в сеть заново. */
    private fun applyFilter() {
        val current = _state.value
        if (current is MatchesUiState.Content) {
            _state.value = current.copy(matches = filter(allMatches))
        }
    }

    private fun filter(matches: List<Match>): List<Match> =
        filterMatchesByTeams(matches, selection.selectedTeamIds.value)
}
