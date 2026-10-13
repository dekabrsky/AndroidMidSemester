package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.data.repository.SelectedTeamsRepository
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
    private val selectedTeams: SelectedTeamsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    // Полный список матчей текущей даты (до фильтрации)
    private var currentDate: LocalDate = LocalDate.now()
    private var allMatches: List<Match> = emptyList()

    init {
        load(currentDate)


        viewModelScope.launch {
            selectedTeams.selectedIds.collect {
                if (allMatches.isNotEmpty() || _state.value is MatchesUiState.Content) {
                    _state.value = MatchesUiState.Content(
                        date = currentDate,
                        matches = applyFilter(allMatches),
                    )
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
                    allMatches = matches
                    _state.value = MatchesUiState.Content(date, applyFilter(matches))
                }
                .onFailure { e ->
                    _state.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }


    private fun applyFilter(matches: List<Match>): List<Match> {
        val ids = selectedTeams.selectedIds.value
        if (ids.isEmpty()) return matches
        return matches.filter { it.home.id in ids || it.away.id in ids }
    }
}