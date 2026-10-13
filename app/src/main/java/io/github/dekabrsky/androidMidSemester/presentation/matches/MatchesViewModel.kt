package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MatchesUiState {
    val date: LocalDate
    data class Loading(override val date: LocalDate) : MatchesUiState
    data class Content(override val date: LocalDate, val matches: List<Match>) : MatchesUiState
    data class Error(override val date: LocalDate, val message: String) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading(LocalDate.now()))
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    private var loadVersion = 0L

    init {
        load(state.value.date)
    }

    fun previousDay() {
        load(state.value.date.minusDays(1))
    }

    fun nextDay() {
        load(state.value.date.plusDays(1))
    }

    private fun load(date: LocalDate) {
        val version = ++loadVersion
        _state.value = MatchesUiState.Loading(date)
        viewModelScope.launch {
            try {
                val matches = getMatches(date)
                if (version == loadVersion) {
                    _state.value = MatchesUiState.Content(date, matches)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (version == loadVersion) {
                    _state.value = MatchesUiState.Error(
                        date = date,
                        message = e.message ?: "Не удалось загрузить матчи",
                    )
                }
            }
        }
    }
}