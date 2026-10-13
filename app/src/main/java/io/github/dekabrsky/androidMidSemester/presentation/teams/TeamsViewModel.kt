package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.data.selection.TeamSelectionStore
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Страна, команды которой грузим (задача: Russia). */
private const val COUNTRY = "Russia"

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(val teams: List<Team>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsViewModel(
    private val getTeams: GetTeamsUseCase,
    private val selection: TeamSelectionStore,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    /** Выбранные команды берём прямо из общего store — экран матчей увидит те же изменения. */
    val selectedTeamIds: StateFlow<Set<Long>> = selection.selectedTeamIds

    init {
        load()
    }

    fun load() {
        _state.value = TeamsUiState.Loading
        viewModelScope.launch {
            runCatching { getTeams(COUNTRY) }
                .onSuccess { _state.value = TeamsUiState.Content(it) }
                .onFailure { e ->
                    _state.value = TeamsUiState.Error(
                        e.message ?: "Не удалось загрузить команды"
                    )
                }
        }
    }

    fun toggleTeam(teamId: Long) = selection.toggle(teamId)

    fun clearSelection() = selection.clear()
}
