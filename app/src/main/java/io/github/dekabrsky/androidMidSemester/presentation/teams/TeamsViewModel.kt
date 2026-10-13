
package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(val teams: List<Team>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsViewModel(
    private val repository: TeamSelectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    val selectedIds = repository.selectedTeamIds

    init {
        loadTeams()
    }

    fun loadTeams() {
        viewModelScope.launch {
            _state.value = TeamsUiState.Loading
            try {
                val teams = repository.getTeamsByCountry("Russia")
                _state.value = TeamsUiState.Content(teams)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = TeamsUiState.Error(
                    e.message ?: "Не удалось загрузить команды"
                )
            }
        }
    }

    fun selectTeam(teamId: Long, selected: Boolean) {
        repository.updateSelection(teamId, selected)
    }
}
