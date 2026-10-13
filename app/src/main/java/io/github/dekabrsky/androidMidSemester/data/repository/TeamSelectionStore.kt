package io.github.dekabrsky.androidMidSemester.domain.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TeamSelectionStore {
    private val _selectedTeamNames = MutableStateFlow<Set<String>>(emptySet())
    val selectedTeamNames = _selectedTeamNames.asStateFlow()

    fun toggle(name: String) {
        _selectedTeamNames.value =
            if (name in _selectedTeamNames.value) {
                _selectedTeamNames.value - name
            } else {
                _selectedTeamNames.value + name
            }
    }

    fun clear() {
        _selectedTeamNames.value = emptySet()
    }
}