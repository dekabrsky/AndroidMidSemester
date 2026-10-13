package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import kotlinx.coroutines.flow.StateFlow

interface TeamSelectionRepository {
    val selectedTeams: StateFlow<Map<Long, Team>>

    fun setSelected(team: Team, selected: Boolean)
    fun clear()
}