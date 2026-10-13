package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

class SStatsTeamRepository(
    private val api: SStatsApi,
) : TeamRepository {

    override suspend fun teams(country: String): List<Team> =
        api.teams(country).data.map { team ->
            Team(
                id = team.id,
                name = team.name,
                logoUrl = LOGO_URL_PATTERN.format(team.id),
            )
        }

    private companion object {
        const val LOGO_URL_PATTERN = "https://sstats.net/assets/logos/%d.png"
    }
}
