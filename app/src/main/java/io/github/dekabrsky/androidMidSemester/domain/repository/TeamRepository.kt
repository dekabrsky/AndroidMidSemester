package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import kotlinx.coroutines.flow.StateFlow

interface TeamRepository {
    val selectedTeamIds: StateFlow<Set<Long>>
    suspend fun getTeams(country: String = "Russia"): List<Team>
    fun toggleTeamSelection(teamId: Long)
    fun clearSelection()
}