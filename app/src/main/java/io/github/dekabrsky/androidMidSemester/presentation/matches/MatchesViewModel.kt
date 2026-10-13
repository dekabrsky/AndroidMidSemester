package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private val currentDate = MutableStateFlow(LocalDate.now())
    private val allMatchesOnDate = MutableStateFlow<List<Match>>(emptyList())
    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                currentDate,
                allMatchesOnDate,
                teamRepository.selectedTeamIds,
                isLoading,
                errorMessage,
            ) { date, matches, selectedIds, loading, error ->
                when {
                    loading -> MatchesUiState.Loading
                    error != null -> MatchesUiState.Error(error)
                    else -> {
                        val filtered = if (selectedIds.isEmpty()) {
                            matches
                        } else {
                            matches.filter { match ->
                                selectedIds.contains(match.home.id) || selectedIds.contains(match.away.id)
                            }
                        }
                        MatchesUiState.Content(date = date, matches = filtered)
                    }
                }
            }.collect { uiState ->
                _state.value = uiState
            }
        }

        load(currentDate.value)
    }

    fun previousDay() {
        val nextDate = currentDate.value.minusDays(1)
        currentDate.value = nextDate
        load(nextDate)
    }

    fun nextDay() {
        val nextDate = currentDate.value.plusDays(1)
        currentDate.value = nextDate
        load(nextDate)
    }

    private fun load(date: LocalDate) {
        isLoading.value = true
        errorMessage.value = null
        viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches ->
                    allMatchesOnDate.value = matches
                    isLoading.value = false
                }
                .onFailure { e ->
                    errorMessage.value = e.message ?: "Не удалось загрузить матчи"
                    isLoading.value = false
                }
        }
    }
}
