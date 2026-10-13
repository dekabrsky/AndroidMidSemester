package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.ConcurrentHashMap

private const val TEAMS_LIMIT = 1000

class TeamRepositoryImpl(
    private val api: SStatsApi,
) : TeamRepository {

    private val teamsByCountry = ConcurrentHashMap<String, List<Team>>()
    private val selectedIds = MutableStateFlow<Set<Long>>(emptySet())

    override val selectedTeamIds: Flow<Set<Long>> = selectedIds

    override suspend fun teamsOf(country: String): List<Team> =
        teamsByCountry[country]
            ?: api.teams(country, TEAMS_LIMIT).data.map { it.toTeam() }
                .also { teams -> teamsByCountry[country] = teams }

    override fun selectTeams(teamIds: Set<Long>) {
        selectedIds.value = teamIds
    }
}
