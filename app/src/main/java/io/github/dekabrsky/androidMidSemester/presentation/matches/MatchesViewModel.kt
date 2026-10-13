package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.data.prefs.SelectedTeamsStore
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
    private val selectedTeamsStore: SelectedTeamsStore,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    private var rawMatches: List<Match> = emptyList()
    private var selectedIds: Set<Long> = emptySet()
    private var currentDate: LocalDate = LocalDate.now()

    init {
        viewModelScope.launch {
            selectedTeamsStore.selectedIds.collect { ids ->
                selectedIds = ids
                applyFilter()
            }
        }
        load(currentDate)
    }

    fun previousDay() {
        val current = (state.value as? MatchesUiState.Content)?.date ?: currentDate
        load(current.minusDays(1))
    }

    fun nextDay() {
        val current = (state.value as? MatchesUiState.Content)?.date ?: currentDate
        load(current.plusDays(1))
    }

    private fun applyFilter() {
        if (rawMatches.isEmpty() && state.value is MatchesUiState.Loading) return
        val filtered = if (selectedIds.isEmpty()) {
            rawMatches
        } else {
            rawMatches.filter { it.home.id in selectedIds || it.away.id in selectedIds }
        }
        _state.value = MatchesUiState.Content(currentDate, filtered)
    }

    private fun load(date: LocalDate) {
        currentDate = date
        _state.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches ->
                    rawMatches = matches
                    applyFilter()
                }
                .onFailure { e ->
                    _state.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }
}