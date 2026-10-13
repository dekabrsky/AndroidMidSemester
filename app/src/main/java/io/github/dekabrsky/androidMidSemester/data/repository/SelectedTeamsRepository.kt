package io.github.dekabrsky.androidMidSemester.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class SelectedTeamsRepository {

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    fun toggle(teamId: Long) {
        val current = _selectedIds.value
        _selectedIds.value = if (teamId in current) current - teamId else current + teamId
    }

    fun clear() {
        _selectedIds.value = emptySet()
    }
}