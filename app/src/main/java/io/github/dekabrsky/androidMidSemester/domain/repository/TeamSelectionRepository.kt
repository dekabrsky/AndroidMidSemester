package io.github.dekabrsky.androidMidSemester.domain.repository

import kotlinx.coroutines.flow.StateFlow
import io.github.dekabrsky.androidMidSemester.domain.model.Team

interface TeamSelectionRepository {
    val selectedTeams: StateFlow<Map<Long, Team>>
    fun setSelected(team: Team, selected: Boolean)
    fun clear()
}