package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamIdsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MatchesUiState {
    data object Loading : MatchesUiState
    data class Content(val date: LocalDate, val matches: List<Match>) : MatchesUiState
    data class Error(val message: String) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
    observeSelectedTeamIds: ObserveSelectedTeamIdsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()
    private val date = MutableStateFlow(LocalDate.now())
    val selectedDate = date.asStateFlow()
    val selectedTeamIds = observeSelectedTeamIds()

    init {
        viewModelScope.launch {
            combine(date, selectedTeamIds) { day, ids -> day to ids }
                .collectLatest { (day, ids) ->
                    _state.value = MatchesUiState.Loading
                    try {
                        _state.value = MatchesUiState.Content(day, getMatches(day, ids))
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        _state.value = MatchesUiState.Error(e.message ?: "Не удалось загрузить матчи")
                    }
                }
        }
    }

    fun previousDay() {
        date.update { it.minusDays(1) }
    }

    fun nextDay() {
        date.update { it.plusDays(1) }
    }
}