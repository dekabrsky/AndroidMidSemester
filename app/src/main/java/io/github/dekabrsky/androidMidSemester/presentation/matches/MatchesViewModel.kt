package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.data.repository.SelectedTeamsHolder
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
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    private var currentDate: LocalDate = LocalDate.now()

    init {
        load(currentDate)
        // Реагируем на изменение выбранных команд
        viewModelScope.launch {
            SelectedTeamsHolder.selectedNames.collect {
                val current = _state.value
                if (current is MatchesUiState.Content) {
                    _state.value = current.copy(matches = filterMatches(current.matches))
                }
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
        _state.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches ->
                    _state.value = MatchesUiState.Content(date, filterMatches(matches))
                }
                .onFailure { e ->
                    _state.value = MatchesUiState.Error(e.message ?: "Не удалось загрузить матчи")
                }
        }
    }

    private fun filterMatches(matches: List<Match>): List<Match> {
        val selected = SelectedTeamsHolder.selectedNames.value
        if (selected.isEmpty()) return matches
        return matches.filter { match ->
            val home = match.home.name.trim().lowercase()
            val away = match.away.name.trim().lowercase()
            selected.any { sel -> home.contains(sel) || sel.contains(home) ||
                    away.contains(sel) || sel.contains(away) }
        }
    }
}