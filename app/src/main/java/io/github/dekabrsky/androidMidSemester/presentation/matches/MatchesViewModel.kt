package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamIdsUseCase
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
    observeSelectedTeamIds: ObserveSelectedTeamIdsUseCase,
) : ViewModel() {

    private val loadedState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    private var currentDate = LocalDate.now()

    val state: StateFlow<MatchesUiState> = combine(
        loadedState,
        observeSelectedTeamIds(),
    ) { currentState, selectedTeamIds ->
        if (currentState is MatchesUiState.Content && selectedTeamIds.isNotEmpty()) {
            currentState.copy(
                matches = currentState.matches.filter { match ->
                    match.home.id in selectedTeamIds || match.away.id in selectedTeamIds
                }
            )
        } else {
            currentState
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MatchesUiState.Loading,
    )

    init {
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
        loadedState.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date) }
                .onSuccess { matches -> loadedState.value = MatchesUiState.Content(date, matches) }
                .onFailure { e ->
                    loadedState.value = MatchesUiState.Error(
                        e.message ?: "Не удалось загрузить матчи"
                    )
                }
        }
    }
}
