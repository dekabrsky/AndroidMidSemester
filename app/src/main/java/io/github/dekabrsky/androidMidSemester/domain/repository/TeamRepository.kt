package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    val selectedTeamIds: Flow<Set<Long>>
    suspend fun teamsOf(country: String): List<Team>
    fun selectTeams(teamIds: Set<Long>)
}
