package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

class TeamRepositoryImpl(
    private val api: SStatsApi,
) : TeamRepository {

    override suspend fun teams(country: String): List<Team> =
        api.teams(country).data.map { it.toTeam() }
}
