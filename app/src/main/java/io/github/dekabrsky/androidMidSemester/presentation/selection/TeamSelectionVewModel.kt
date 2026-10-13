package io.github.dekabrsky.androidMidSemester.presentation.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.SettingsRepository
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamSelectionViewModel(
    private val getTeams: GetTeamsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeamSelectionUiState>(TeamSelectionUiState.Loading)
    val uiState: StateFlow<TeamSelectionUiState> = _uiState.asStateFlow()

    private var _favoriteIds = MutableStateFlow<Set<Long>>(emptySet())

    init {
        loadTeams()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            settingsRepository.favoriteTeamIds.collect { ids ->
                _favoriteIds.value = ids
                // Обновляем состояние UI, чтобы галочки перерисовались
                if (_uiState.value is TeamSelectionUiState.Success) {
                    val currentTeams = (_uiState.value as TeamSelectionUiState.Success).teams
                    _uiState.value = TeamSelectionUiState.Success(currentTeams, ids)
                }
            }
        }
    }

    private fun loadTeams() {
        viewModelScope.launch {
            runCatching { getTeams("Russia") }
                .onSuccess { teams ->
                    _uiState.value = TeamSelectionUiState.Success(teams, _favoriteIds.value)
                }
                .onFailure {
                    _uiState.value = TeamSelectionUiState.Error("Не удалось загрузить команды")
                }
        }
    }

    fun toggleTeam(teamId: Long) {
        val current = _favoriteIds.value
        val updated = if (current.contains(teamId)) {
            current - teamId
        } else {
            current + teamId
        }
        viewModelScope.launch {
            settingsRepository.setFavoriteTeamIds(updated)
        }
    }
}

sealed interface TeamSelectionUiState {
    data object Loading : TeamSelectionUiState
    data class Error(val message: String) : TeamSelectionUiState
    data class Success(val teams: List<Team>, val favoriteIds: Set<Long>) : TeamSelectionUiState
}