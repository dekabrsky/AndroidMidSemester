package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.domain.model.Team

class TeamRepository(
    private val api: SStatsApi,
) {
    suspend fun getRussianTeams(): List<Team> =
        api.teamsByCountry("Russia").data.map { dto ->
            Team(id = dto.id, name = dto.name)
        }
}