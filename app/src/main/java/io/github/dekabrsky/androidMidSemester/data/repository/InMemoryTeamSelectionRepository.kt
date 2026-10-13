package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryTeamSelectionRepository : TeamSelectionRepository {
    private val _selectedTeamIds = MutableStateFlow<Set<Long>>(emptySet())
    override val selectedTeamIds: StateFlow<Set<Long>> = _selectedTeamIds.asStateFlow()

    override fun toggle(teamId: Long) {
        _selectedTeamIds.value = _selectedTeamIds.value.toMutableSet().apply {
            if (!add(teamId)) remove(teamId)
        }
    }
}
