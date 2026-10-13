package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SStatsTeamRepositoryImpl(private val api: SStatsApi) : TeamRepository {
    private val cachedTeams = mutableMapOf<String, List<Team>>()
    private val mutex = Mutex()

    override suspend fun teamsIn(country: String): List<Team> = mutex.withLock {
        cachedTeams[country] ?: api.teamsIn(country, limit = 50).data
            .map { it.toTeam() }.also { cachedTeams[country] = it }
    }
}
