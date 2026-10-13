package io.github.dekabrsky.androidMidSemester.presentation.team

import io.github.dekabrsky.androidMidSemester.domain.model.Team

sealed interface TeamSelectionUiState {
    data object Loading : TeamSelectionUiState
    data class Success(val teams: List<Team>) : TeamSelectionUiState
    data class Error(val message: String) : TeamSelectionUiState
}