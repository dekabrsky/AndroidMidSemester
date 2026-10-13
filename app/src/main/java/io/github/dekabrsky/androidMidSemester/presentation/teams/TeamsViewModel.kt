package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(val teams: List<Team>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsViewModel(private val repository: TeamRepository) : ViewModel() {
    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state = _state.asStateFlow()
    val selectedTeamIds = repository.selectedTeamIds

    init {
        viewModelScope.launch {
            runCatching { repository.teams() }
                .onSuccess { _state.value = TeamsUiState.Content(it) }
                .onFailure {
                    _state.value = TeamsUiState.Error(it.message ?: "Не удалось загрузить команды")
                }
        }
    }

    fun toggleTeam(id: Long) {
        val ids = selectedTeamIds.value
        repository.selectTeams(if (id in ids) ids - id else ids + id)
    }

    fun clearSelection() = repository.selectTeams(emptySet())
}
