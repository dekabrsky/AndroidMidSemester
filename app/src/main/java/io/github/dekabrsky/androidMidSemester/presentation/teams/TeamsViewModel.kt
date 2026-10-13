package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(
        val teams: List<Team>,
        val selectedIds: Set<Long>,
        val searchQuery: String,
    ) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsViewModel(
    private val teamRepository: TeamRepository,
) : ViewModel() {

    private val _allTeams = MutableStateFlow<List<Team>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    init {
        loadTeams()

        viewModelScope.launch {
            combine(_allTeams, teamRepository.getSelectedTeamIds(), _searchQuery) { teams, selectedIds, query ->
                val filteredTeams = if (query.isBlank()) {
                    teams
                } else {
                    teams.filter { it.name.contains(query, ignoreCase = true) }
                }
                TeamsUiState.Content(
                    teams = filteredTeams,
                    selectedIds = selectedIds,
                    searchQuery = query,
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleTeam(teamId: Long) {
        teamRepository.toggleTeamSelection(teamId)
    }

    fun clearSelection() {
        teamRepository.clearSelection()
    }

    private fun loadTeams() {
        _state.value = TeamsUiState.Loading
        viewModelScope.launch {
            runCatching { teamRepository.getTeams("Russia") }
                .onSuccess { teams ->
                    _allTeams.value = teams
                }
                .onFailure { e ->
                    _state.value = TeamsUiState.Error(
                        e.message ?: "Не удалось загрузить список команд"
                    )
                }
        }
    }
}
