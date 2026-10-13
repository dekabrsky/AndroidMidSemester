package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetRussianTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamIdsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.SaveSelectedTeamIdsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TeamSelectionUiState {
    data object Loading : TeamSelectionUiState
    data class Error(val message: String) : TeamSelectionUiState
    data class Content(
        val teams: List<Team>,
        val selectedIds: Set<Long>,
        val query: String = "",
    ) : TeamSelectionUiState {
        val visibleTeams: List<Team>
            get() = teams.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }
}

class TeamSelectionViewModel(
    private val getRussianTeams: GetRussianTeamsUseCase,
    private val observeSelectedTeamIds: ObserveSelectedTeamIdsUseCase,
    private val saveSelectedTeamIds: SaveSelectedTeamIdsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<TeamSelectionUiState>(TeamSelectionUiState.Loading)
    val state = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        loadTeams()
    }

    fun loadTeams() {
        loadJob?.cancel()
        _state.value = TeamSelectionUiState.Loading
        loadJob = viewModelScope.launch {
            try {
                val teams = getRussianTeams()
                _state.value = TeamSelectionUiState.Content(
                    teams = teams,
                    selectedIds = observeSelectedTeamIds().value.intersect(teams.map { it.id }.toSet()),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = TeamSelectionUiState.Error(e.message ?: "Не удалось загрузить команды")
            }
        }
    }

    fun search(query: String) {
        val current = _state.value as? TeamSelectionUiState.Content ?: return
        _state.value = current.copy(query = query)
    }

    fun toggleTeam(id: Long) {
        val current = _state.value as? TeamSelectionUiState.Content ?: return
        if (current.teams.none { it.id == id }) return
        val selected = if (id in current.selectedIds) current.selectedIds - id else current.selectedIds + id
        _state.value = current.copy(selectedIds = selected)
    }

    fun clearSelection() {
        val current = _state.value as? TeamSelectionUiState.Content ?: return
        _state.value = current.copy(selectedIds = emptySet())
    }

    fun applySelection() {
        val current = _state.value as? TeamSelectionUiState.Content ?: return
        saveSelectedTeamIds(current.selectedIds)
    }
}