package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import kotlinx.coroutines.flow.StateFlow

interface TeamRepository {
    suspend fun getTeams(country: String = "Russia"): List<Team>
    fun getSelectedTeamIds(): StateFlow<Set<Long>>
    fun toggleTeamSelection(teamId: Long)
    fun clearSelection()
}
