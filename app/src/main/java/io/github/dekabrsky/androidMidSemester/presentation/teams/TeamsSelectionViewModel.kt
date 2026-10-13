package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.data.repository.SelectedTeamsRepository
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsByCountryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val COUNTRY = "Russia"

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(
        val teams: List<Team>,
        val selectedIds: Set<Long>,
    ) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsSelectionViewModel(
    private val getTeamsByCountry: GetTeamsByCountryUseCase,
    private val selectedRepository: SelectedTeamsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    private var allTeams: List<Team> = emptyList()

    init {
        load()
        // Следим за изменением выбранных — обновляем UI
        viewModelScope.launch {
            selectedRepository.selectedIds.collect { ids ->
                val current = _state.value
                if (current is TeamsUiState.Content) {
                    _state.value = current.copy(selectedIds = ids)
                }
            }
        }
    }

    fun toggle(teamId: Long) {
        selectedRepository.toggle(teamId)
    }

    fun reload() = load()

    private fun load() {
        _state.value = TeamsUiState.Loading
        viewModelScope.launch {
            runCatching { getTeamsByCountry(COUNTRY) }
                .onSuccess { teams ->
                    allTeams = teams
                    _state.value = TeamsUiState.Content(
                        teams = teams,
                        selectedIds = selectedRepository.selectedIds.value,
                    )
                }
                .onFailure { e ->
                    _state.value = TeamsUiState.Error(e.message ?: "Не удалось загрузить команды")
                }
        }
    }
}