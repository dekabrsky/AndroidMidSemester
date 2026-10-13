package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.POPULAR_TEAM_IDS
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.ClearTeamSelectionUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.ToggleTeamSelectionUseCase
import kotlinx.coroutines.CancellationException
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
        val selectedIds: Set<Long>,
        val query: String,
        val showAll: Boolean,
        val totalCount: Int,
    ) : TeamsUiState {
        val canToggleShowAll: Boolean get() = query.isBlank()
    }

    data class Error(val message: String) : TeamsUiState
}

private sealed interface TeamsLoad {
    data object Loading : TeamsLoad
    data class Loaded(val teams: List<Team>) : TeamsLoad
    data class Failed(val message: String) : TeamsLoad
}

class TeamsViewModel(
    private val getTeams: GetTeamsUseCase,
    observeSelectedTeams: ObserveSelectedTeamsUseCase,
    private val toggleTeamSelection: ToggleTeamSelectionUseCase,
    private val clearTeamSelection: ClearTeamSelectionUseCase,
) : ViewModel() {

    private val load = MutableStateFlow<TeamsLoad>(TeamsLoad.Loading)
    private val query = MutableStateFlow("")
    private val showAll = MutableStateFlow(false)

    val state: StateFlow<TeamsUiState> =
        combine(load, observeSelectedTeams(), query, showAll) { load, selectedIds, query, showAll ->
            when (load) {
                TeamsLoad.Loading -> TeamsUiState.Loading
                is TeamsLoad.Failed -> TeamsUiState.Error(load.message)
                is TeamsLoad.Loaded -> TeamsUiState.Content(
                    teams = visibleTeams(load.teams, selectedIds, query, showAll),
                    selectedIds = selectedIds,
                    query = query,
                    showAll = showAll,
                    totalCount = load.teams.size,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TeamsUiState.Loading,
        )

    init {
        loadTeams()
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onTeamToggle(teamId: Long) {
        toggleTeamSelection(teamId)
    }

    fun onClearSelection() {
        clearTeamSelection()
    }

    fun onToggleShowAll() {
        showAll.value = !showAll.value
    }

    fun retry() {
        loadTeams()
    }

    private fun loadTeams() {
        load.value = TeamsLoad.Loading
        viewModelScope.launch {
            load.value = try {
                TeamsLoad.Loaded(getTeams())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                TeamsLoad.Failed(e.message ?: "Не удалось загрузить команды")
            }
        }
    }

    private fun visibleTeams(
        all: List<Team>,
        selectedIds: Set<Long>,
        query: String,
        showAll: Boolean,
    ): List<Team> {
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) return all.filter { it.name.contains(trimmed, ignoreCase = true) }
        if (showAll) return all

        val byId = all.associateBy { it.id }
        val popular = POPULAR_TEAM_IDS.mapNotNull { byId[it] }
        val selectedOthers = all.filter { it.id in selectedIds && it.id !in POPULAR_TEAM_IDS }
        return popular + selectedOthers
    }
}