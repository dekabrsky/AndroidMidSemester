package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import kotlinx.coroutines.flow.StateFlow

interface TeamRepository {
    suspend fun russianTeams(): List<Team>
    fun selectedTeamIds(): Set<Long>
    fun setSelectedTeamIds(ids: Set<Long>)
    fun selectedTeamIdsFlow(): StateFlow<Set<Long>>
}