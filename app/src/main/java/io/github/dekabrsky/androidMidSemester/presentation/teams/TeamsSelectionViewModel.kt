package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.SaveSelectedTeamsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Error(val message: String) : TeamsUiState
    data class Content(
        val teams: List<Team>,
        val selectedIds: Set<Long>,
        val query: String = "",
    ) : TeamsUiState {
        val visibleTeams: List<Team>
            get() = if (query.isBlank()) teams else teams.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }
}

class TeamsSelectionViewModel(
    private val getTeams: GetTeamsUseCase,
    private val observeSelectedTeams: ObserveSelectedTeamsUseCase,
    private val saveSelectedTeams: SaveSelectedTeamsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = TeamsUiState.Loading
        viewModelScope.launch {
            runCatching { getTeams() }
                .onSuccess { teams ->
                    val knownIds = teams.map { it.id }.toSet()
                    _state.value = TeamsUiState.Content(
                        teams = teams,
                        selectedIds = observeSelectedTeams().value intersect knownIds,
                    )
                }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    _state.value = TeamsUiState.Error(e.message ?: "Не удалось загрузить команды")
                }
        }
    }

    fun onQueryChange(query: String) = updateContent { it.copy(query = query) }

    fun onTeamToggle(teamId: Long) = updateContent { content ->
        val selected = content.selectedIds
        content.copy(selectedIds = if (teamId in selected) selected - teamId else selected + teamId)
    }

    fun onClearSelection() = updateContent { it.copy(selectedIds = emptySet()) }

    fun onApply() {
        val content = _state.value as? TeamsUiState.Content ?: return
        saveSelectedTeams(content.selectedIds)
    }

    private inline fun updateContent(crossinline transform: (TeamsUiState.Content) -> TeamsUiState.Content) {
        _state.update { current -> if (current is TeamsUiState.Content) transform(current) else current }
    }
}
