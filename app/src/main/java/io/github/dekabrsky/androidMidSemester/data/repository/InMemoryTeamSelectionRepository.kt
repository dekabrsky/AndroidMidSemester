package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryTeamSelectionRepository : TeamSelectionRepository {
    private val selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    override val selectedTeamIds = selectedIds.asStateFlow()

    override fun toggle(teamId: Long) {
        selectedIds.update { ids ->
            if (teamId in ids) ids - teamId else ids + teamId
        }
    }

    override fun clear() {
        selectedIds.value = emptySet()
    }
}
