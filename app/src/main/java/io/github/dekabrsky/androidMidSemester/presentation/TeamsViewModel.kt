package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.data.repository.TeamRepository
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(val teams: List<Team>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsViewModel(
    private val repository: TeamRepository,
    private val selectionStore: TeamSelectionStore,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    val selectedTeamNames = selectionStore.selectedTeamNames

    init {
        loadTeams()
    }

    fun toggleTeam(name: String) {
        selectionStore.toggle(name)
    }

    fun clearSelection() {
        selectionStore.clear()
    }

    fun retry() {
        loadTeams()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            _state.value = TeamsUiState.Loading

            runCatching {
                repository.getRussianTeams()
            }.onSuccess { teams ->
                _state.value = TeamsUiState.Content(teams.sortedBy { it.name })
            }.onFailure { error ->
                _state.value = TeamsUiState.Error(
                    error.message ?: "Не удалось загрузить команды"
                )
            }
        }
    }
}