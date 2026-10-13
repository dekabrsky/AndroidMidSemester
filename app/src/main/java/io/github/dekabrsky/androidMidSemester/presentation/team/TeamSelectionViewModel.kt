package io.github.dekabrsky.androidMidSemester.presentation.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeamSelectionViewModel(
    private val teamRepository: TeamRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeamSelectionUiState>(TeamSelectionUiState.Loading)
    val uiState: StateFlow<TeamSelectionUiState> = _uiState.asStateFlow()

    val selectedTeamIds = teamRepository.selectedTeamIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        loadTeams()
    }

    fun loadTeams() {
        viewModelScope.launch {
            _uiState.value = TeamSelectionUiState.Loading
            try {
                val teams = teamRepository.getTeams(country = "Russia")
                _uiState.value = TeamSelectionUiState.Success(teams)
            } catch (e: Exception) {
                _uiState.value = TeamSelectionUiState.Error(e.message ?: "Ошибка загрузки команд")
            }
        }
    }

    fun onTeamToggled(teamId: Long) {
        teamRepository.toggleTeamSelection(teamId)
    }

    fun onClearFilter() {
        teamRepository.clearSelection()
    }
}