package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.FilterMatchesByTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MatchesUiState {
    data object Loading : MatchesUiState
    data class Content(val date: LocalDate, val matches: List<Match>) : MatchesUiState
    data class Error(val message: String) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
    selectionRepository: TeamSelectionRepository,
    private val filterMatches: FilterMatchesByTeamsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val selectedTeamIds = selectionRepository.selectedTeamIds
    val state: StateFlow<MatchesUiState> = combine(_state, selectedTeamIds) { state, ids ->
        if (state is MatchesUiState.Content) {
            state.copy(matches = filterMatches(state.matches, ids))
        } else {
            state
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MatchesUiState.Loading)

    init {
        load(LocalDate.now())
    }

    fun previousDay() {
        val current = (_state.value as? MatchesUiState.Content)?.date ?: LocalDate.now()
        load(current.minusDays(1))
    }

    fun nextDay() {
        val current = (_state.value as? MatchesUiState.Content)?.date ?: LocalDate.now()
        load(current.plusDays(1))
    }

    private fun load(date: LocalDate) {
        _state.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches -> _state.value = MatchesUiState.Content(date, matches) }
                .onFailure { e ->
                    _state.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }
}
