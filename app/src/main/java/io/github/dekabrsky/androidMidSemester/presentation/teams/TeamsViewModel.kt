package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.SelectTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(val teams: List<Team>, val selectedIds: Set<Long>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsViewModel(
    private val getTeams: GetTeamsUseCase,
    private val observeSelectedTeams: ObserveSelectedTeamsUseCase,
    private val selectTeams: SelectTeamsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { TeamsUiState.Content(getTeams(), observeSelectedTeams().first()) }
                .onSuccess { content -> _state.value = content }
                .onFailure { e ->
                    _state.value = TeamsUiState.Error(
                        e.message ?: "Не удалось загрузить команды"
                    )
                }
        }
    }

    fun toggle(team: Team) {
        _state.update { current ->
            if (current is TeamsUiState.Content) {
                val selectedIds = if (team.id in current.selectedIds) {
                    current.selectedIds - team.id
                } else {
                    current.selectedIds + team.id
                }
                current.copy(selectedIds = selectedIds)
            } else {
                current
            }
        }
    }

    fun clear() {
        _state.update { current ->
            if (current is TeamsUiState.Content) current.copy(selectedIds = emptySet()) else current
        }
    }

    fun save() {
        (state.value as? TeamsUiState.Content)?.let { content -> selectTeams(content.selectedIds) }
    }
}
