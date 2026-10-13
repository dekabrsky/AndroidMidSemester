package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.FilterMatchesUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MatchesUiState {
    val date: LocalDate

    data class Loading(override val date: LocalDate) : MatchesUiState
    data class Content(
        override val date: LocalDate,
        val matches: List<Match>,
        val isFilterActive: Boolean = false,
    ) : MatchesUiState
    data class Error(override val date: LocalDate, val cause: Throwable) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
    private val filterMatches: FilterMatchesUseCase,
    selectionRepository: TeamSelectionRepository,
) : ViewModel() {

    private val loadedState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading(LocalDate.now()))

    val state: StateFlow<MatchesUiState> = combine(
        loadedState,
        selectionRepository.selectedTeams,
    ) { loaded, selected ->
        when (loaded) {
            is MatchesUiState.Content -> loaded.copy(
                matches = filterMatches(loaded.matches, selected.keys),
                isFilterActive = selected.isNotEmpty(),
            )
            else -> loaded
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, loadedState.value)

    private var loadVersion = 0L

    init {
        load(loadedState.value.date)
    }

    fun previousDay() {
        load(loadedState.value.date.minusDays(1))
    }

    fun nextDay() {
        load(loadedState.value.date.plusDays(1))
    }

    fun retry() {
        if (loadedState.value is MatchesUiState.Error) load(loadedState.value.date)
    }

    private fun load(date: LocalDate) {
        val version = ++loadVersion
        loadedState.value = MatchesUiState.Loading(date)

        viewModelScope.launch {
            try {
                val matches = getMatches(date)
                if (version == loadVersion) {
                    loadedState.value = MatchesUiState.Content(date, matches)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (version == loadVersion) {
                    loadedState.value = MatchesUiState.Error(date = date, cause = e)
                }
            }
        }
    }
}