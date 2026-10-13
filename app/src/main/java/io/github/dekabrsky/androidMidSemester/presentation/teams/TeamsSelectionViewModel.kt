package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.data.prefs.SelectedTeamsStore
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data class Content(val teams: List<Team>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

class TeamsSelectionViewModel(
    private val getTeams: GetTeamsUseCase,
    private val store: SelectedTeamsStore,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val state: StateFlow<TeamsUiState> = _state.asStateFlow()

    val selectedIds: StateFlow<Set<Long>> = store.selectedIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        viewModelScope.launch {
            runCatching { getTeams("RU") }
                .onSuccess { _state.value = TeamsUiState.Content(it) }
                .onFailure { _state.value = TeamsUiState.Error(it.message ?: "Ошибка загрузки") }
        }
    }

    fun toggle(id: Long) {
        viewModelScope.launch { store.toggle(id) }
    }

    fun clearAll() {
        viewModelScope.launch { store.clear() }
    }
}