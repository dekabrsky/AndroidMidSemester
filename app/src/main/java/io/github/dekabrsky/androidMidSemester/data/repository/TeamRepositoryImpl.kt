package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "TeamRepository"

class TeamRepositoryImpl(
    private val api: SStatsApi,
) : TeamRepository {

    private val mutex = Mutex()
    private val teamsByCountry = mutableMapOf<String, List<Team>>()

    override suspend fun teamsOf(country: String): List<Team> = mutex.withLock {
        teamsByCountry[country] ?: run {
            val response = api.teams(country)
            val teams = response.data
                .filter { it.country?.name.equals(country, ignoreCase = true) }
                .map { it.toTeam() }
            Log.d(TAG, "Country=$country: с сервера ${response.data.size}, после фильтра ${teams.size}")
            teams.also { teamsByCountry[country] = it }
        }
    }
}
