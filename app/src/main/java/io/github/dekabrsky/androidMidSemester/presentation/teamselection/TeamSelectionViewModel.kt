package io.github.dekabrsky.androidMidSemester.presentation.teamselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.data.repository.SelectedTeamsHolder
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TeamSelectionUiState {
    data object Loading : TeamSelectionUiState
    data class Content(val teams: List<Team>, val selectedNames: Set<String>) : TeamSelectionUiState
    data class Error(val message: String) : TeamSelectionUiState
}

class TeamSelectionViewModel(
    private val getTeams: GetTeamsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamSelectionUiState>(TeamSelectionUiState.Loading)
    val state: StateFlow<TeamSelectionUiState> = _state.asStateFlow()

    init {
        load()
        viewModelScope.launch {
            SelectedTeamsHolder.selectedNames.collect { selected ->
                val current = _state.value as? TeamSelectionUiState.Content ?: return@collect
                _state.value = current.copy(selectedNames = selected)
            }
        }
    }

    fun load() {
        _state.value = TeamSelectionUiState.Loading
        viewModelScope.launch {
            runCatching { getTeams("Russia") }
                .onSuccess { teams ->
                    _state.value = TeamSelectionUiState.Content(
                        teams = teams,
                        selectedNames = SelectedTeamsHolder.selectedNames.value,
                    )
                }
                .onFailure { e ->
                    _state.value = TeamSelectionUiState.Error(e.message ?: "Не удалось загрузить команды")
                }
        }
    }

    fun onTeamClicked(teamName: String) {
        SelectedTeamsHolder.toggle(teamName)
    }
}