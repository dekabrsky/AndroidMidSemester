package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
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

    data class Error(
        val message: String
    ) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
) : ViewModel() {

    private val _state =
        MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)

    val state: StateFlow<MatchesUiState> =
        _state.asStateFlow()

    init {
        load(LocalDate.now())
    }

    fun previousDay() {
        val current =
            (state.value as? MatchesUiState.Content)?.date
                ?: LocalDate.now()

        load(current.minusDays(1))
    }

    fun nextDay() {
        val current =
            (state.value as? MatchesUiState.Content)?.date
                ?: LocalDate.now()

        load(current.plusDays(1))
    }

    private fun load(date: LocalDate) {
        _state.value = MatchesUiState.Loading

        viewModelScope.launch {
            runCatching {
                getMatches(date)
            }.onSuccess { matches ->

                _state.value = MatchesUiState.Content(
                    date,
                    matches
                )

            }.onFailure { e ->

                _state.value = MatchesUiState.Error(
                    e.message ?: "Не удалось загрузить матчи"
                )
            }
        }
    }
}