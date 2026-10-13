package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    observeSelectedTeams: ObserveSelectedTeamsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    private val date = MutableStateFlow(LocalDate.now())
    private var lastRequest: Pair<LocalDate, Set<Long>>? = null

    init {
        combine(date, observeSelectedTeams(), ::Pair)
            .onEach(::load)
            .launchIn(viewModelScope)
    }

    fun previousDay() {
        date.update { it.minusDays(1) }
    }

    fun nextDay() {
        date.update { it.plusDays(1) }
    }

    private fun load(request: Pair<LocalDate, Set<Long>>) {
        val (date, teamIds) = request
        lastRequest = request
        _state.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date, teamIds) }
                .onSuccess { matches ->
                    if (lastRequest == request) {
                        _state.value = MatchesUiState.Content(date, matches)
                    }
                }
                .onFailure { e ->
                    if (lastRequest == request) {
                        _state.value = MatchesUiState.Error(
                            e.message ?: "Не удалось загрузить матчи"
                        )
                    }
                }
        }
    }
}
