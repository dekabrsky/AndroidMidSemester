package io.github.dekabrsky.androidMidSemester.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface TeamSelectionRepository {
    val selectedTeamIds: StateFlow<Set<Long>>
    fun setSelectedTeamIds(ids: Set<Long>)
}
