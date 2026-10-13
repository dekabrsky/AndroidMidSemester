package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team

interface TeamRepository {
    suspend fun russianTeams(): List<Team>
    fun selectedTeamIds(): Set<Long>
    fun saveSelectedTeamIds(ids: Set<Long>)
}
