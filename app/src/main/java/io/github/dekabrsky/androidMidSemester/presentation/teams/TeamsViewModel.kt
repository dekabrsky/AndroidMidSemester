package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetRussianTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(val teams: List<Team>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsViewModel(
    private val getRussianTeams: GetRussianTeamsUseCase,
    private val selectionRepository: TeamSelectionRepository,
) : ViewModel() {
    private val currentState = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state = currentState.asStateFlow()
    val selectedTeamIds = selectionRepository.selectedTeamIds

    init {
        load()
    }

    fun toggleTeam(id: Long) = selectionRepository.toggle(id)

    fun clearSelection() = selectionRepository.clear()

    fun load() {
        currentState.value = TeamsUiState.Loading
        viewModelScope.launch {
            runCatching { getRussianTeams() }
                .onSuccess { currentState.value = TeamsUiState.Content(it) }
                .onFailure { error ->
                    currentState.value = TeamsUiState.Error(
                        error.message ?: "Не удалось загрузить команды"
                    )
                }
        }
    }
}
