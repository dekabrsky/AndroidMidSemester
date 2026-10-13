package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamIdsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ToggleTeamSelectionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(
        val teams: List<Team>,
        val selectedTeamIds: Set<Long>,
    ) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsViewModel(
    private val getTeams: GetTeamsUseCase,
    observeSelectedTeamIds: ObserveSelectedTeamIdsUseCase,
    private val toggleTeamSelection: ToggleTeamSelectionUseCase,
) : ViewModel() {

    private val teamsState = MutableStateFlow<TeamsLoadState>(TeamsLoadState.Loading)

    val state: StateFlow<TeamsUiState> = combine(
        teamsState,
        observeSelectedTeamIds(),
    ) { loadState, selectedIds ->
        when (loadState) {
            TeamsLoadState.Loading -> TeamsUiState.Loading
            is TeamsLoadState.Content -> TeamsUiState.Content(loadState.teams, selectedIds)
            is TeamsLoadState.Error -> TeamsUiState.Error(loadState.message)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TeamsUiState.Loading,
    )

    init {
        viewModelScope.launch {
            runCatching { getTeams(RUSSIA) }
                .onSuccess { teams -> teamsState.value = TeamsLoadState.Content(teams) }
                .onFailure { error ->
                    teamsState.value = TeamsLoadState.Error(
                        error.message ?: "Не удалось загрузить команды"
                    )
                }
        }
    }

    fun toggleTeam(teamId: Long) = toggleTeamSelection(teamId)

    private sealed interface TeamsLoadState {
        data object Loading : TeamsLoadState
        data class Content(val teams: List<Team>) : TeamsLoadState
        data class Error(val message: String) : TeamsLoadState
    }

    private companion object {
        const val RUSSIA = "Russia"
    }
}
