package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(val teams: List<Team>, val selectedIds: Set<Long>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsViewModel(
    private val teamRepository: TeamRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { teamRepository.russianTeams() }
                .onSuccess { teams ->
                    _state.value = TeamsUiState.Content(
                        teams = teams,
                        selectedIds = teamRepository.selectedTeamIds.value,
                    )
                }
                .onFailure { error ->
                    _state.value = TeamsUiState.Error(
                        error.message ?: "Не удалось загрузить команды"
                    )
                }
        }
    }

    fun toggleTeam(teamId: Long) {
        val content = _state.value as? TeamsUiState.Content ?: return
        val selected = content.selectedIds.toMutableSet().apply {
            if (!add(teamId)) remove(teamId)
        }
        _state.value = content.copy(selectedIds = selected)
    }

    fun saveSelection() {
        val content = _state.value as? TeamsUiState.Content ?: return
        teamRepository.updateSelectedTeamIds(content.selectedIds)
    }
}
