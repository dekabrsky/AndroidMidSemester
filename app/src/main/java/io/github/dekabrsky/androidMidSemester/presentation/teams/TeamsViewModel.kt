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

    /**
     * @param teams команды, которые сейчас видны на экране
     * @param selectedIds выбранные команды (по всему списку, а не только по видимым)
     * @param query строка поиска
     * @param showAll true, если пользователь раскрыл полный список
     * @param totalCount сколько всего команд пришло с сервера (для кнопки "Показать все")
     */
    data class Content(
        val teams: List<Team>,
        val selectedIds: Set<Long>,
        val query: String,
        val showAll: Boolean,
        val totalCount: Int,
    ) : TeamsUiState {
        /** Кнопку "Показать все / Только популярные" прячем во время поиска: поиск и так идёт по всем. */
        val canToggleShowAll: Boolean get() = query.isBlank()
    }

    data class Error(val message: String) : TeamsUiState
}

/** Результат загрузки списка команд, отдельно от выбора и поиска. */
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

    /**
     * Что показать на экране:
     * - есть строка поиска: ищем по всем командам;
     * - раскрыт полный список: все команды в порядке сервера;
     * - иначе 10 популярных в порядке [POPULAR_TEAM_IDS], а за ними уже выбранные
     *   непопулярные команды, чтобы их можно было увидеть и снять галочку.
     */
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
