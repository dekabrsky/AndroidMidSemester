package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import kotlinx.coroutines.flow.StateFlow

interface TeamRepository {
    val selectedTeamIds: StateFlow<Set<Long>>
    suspend fun teams(): List<Team>
    fun selectTeams(ids: Set<Long>)
}
