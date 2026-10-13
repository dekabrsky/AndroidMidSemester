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
    data class Content(val teams: List<Team>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamSelectionViewModel(private val repository: TeamRepository) : ViewModel() {
    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()
    private val _selectedIds = MutableStateFlow(repository.selectedTeamIds())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repository.russianTeams() }
                .onSuccess { teams ->
                    _state.value = if (teams.isEmpty()) TeamsUiState.Error("Список российских команд пуст") else TeamsUiState.Content(teams)
                }
                .onFailure { error -> _state.value = TeamsUiState.Error(error.message ?: "Не удалось загрузить команды") }
        }
    }

    fun toggle(id: Long) {
        _selectedIds.value = _selectedIds.value.toMutableSet().apply {
            if (!add(id)) remove(id)
        }
    }

    fun save() = repository.saveSelectedTeamIds(_selectedIds.value)
}
