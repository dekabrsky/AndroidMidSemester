package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "TeamRepo"

class TeamRepositoryImpl(
    private val api: SStatsApi,
) : TeamRepository {

    private val cache = ConcurrentHashMap<String, List<Team>>()

    override suspend fun teamsInCountry(code: String): List<Team> {
        cache[code]?.let {
            Log.d(TAG, "HIT  teams country=$code")
            return it
        }
        Log.d(TAG, "MISS teams country=$code — сеть (sstats)")
        val teams = api.teamsByCountry(code).data.map { dto ->
            Team(
                id = dto.id,
                name = dto.name,
                logoUrl = dto.logoUrl,
            )
        }
        cache[code] = teams
        return teams
    }
}