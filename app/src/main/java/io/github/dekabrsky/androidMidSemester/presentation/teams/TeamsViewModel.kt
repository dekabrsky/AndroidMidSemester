package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetRussianTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ToggleTeamSelectionUseCase
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
    private val getRussianTeams: GetRussianTeamsUseCase,
    observeSelectedTeams: ObserveSelectedTeamsUseCase,
    private val toggleTeamSelection: ToggleTeamSelectionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    val selectedTeamIds: StateFlow<Set<Long>> = observeSelectedTeams()

    init {
        viewModelScope.launch {
            runCatching { getRussianTeams() }
                .onSuccess { teams -> _state.value = TeamsUiState.Content(teams) }
                .onFailure { e ->
                    _state.value = TeamsUiState.Error(e.message ?: "Не удалось загрузить команды")
                }
        }
    }

    fun toggle(team: Team) {
        toggleTeamSelection(team.id)
    }
}
