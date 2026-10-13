package io.github.dekabrsky.androidMidSemester.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.SStatsTeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

private const val TEAM_REQUEST_LOG_TAG = "SStatsTeams"
private const val S_STATS_TEAMS_URL = "https://api.sstats.net/Teams/list"

class TeamRepositoryImpl(
    private val api: SStatsApi,
    context: Context,
) : TeamRepository {
    private val preferences = context.getSharedPreferences("team_selection", Context.MODE_PRIVATE)
    private val isDebugBuild = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

    override suspend fun russianTeams(): List<Team> {
        val teams = mutableListOf<SStatsTeamDto>()
        var offset = 0
        var totalCount: Int
        do {
            val requestUrl = "$S_STATS_TEAMS_URL?Country=Russia&offset=$offset&limit=100"
            if (isDebugBuild) Log.d(TEAM_REQUEST_LOG_TAG, "GET $requestUrl")
            val response = try {
                api.teams(offset = offset)
            } catch (error: Exception) {
                if (isDebugBuild) {
                    Log.e(TEAM_REQUEST_LOG_TAG, "Request failed: $requestUrl (${error.message})", error)
                }
                throw error
            }
            if (isDebugBuild) {
                Log.d(TEAM_REQUEST_LOG_TAG, "HTTP ${response.code()} $requestUrl")
            }
            val body = response.body()
            if (!response.isSuccessful || body == null) {
                if (isDebugBuild) {
                    Log.e(TEAM_REQUEST_LOG_TAG, "Team request returned no usable body (HTTP ${response.code()})")
                }
                error("SStats HTTP ${response.code()} while loading Russian teams")
            }
            if (body.status != "OK") {
                if (isDebugBuild) Log.e(TEAM_REQUEST_LOG_TAG, "SStats API error: ${body.status}")
                error("SStats вернул ошибку: ${body.status}")
            }
            teams += body.data
            totalCount = body.totalCount
            offset += body.count
            if (isDebugBuild) {
                Log.d(
                    TEAM_REQUEST_LOG_TAG,
                    "API status=${body.status}, returned=${body.count}, sample=${body.data.take(5).joinToString { it.name }}",
                )
            }
            if (body.count == 0) break
        } while (offset < totalCount)
        if (isDebugBuild) {
            Log.d(
                TEAM_REQUEST_LOG_TAG,
                "Loaded ${teams.size} Russian teams total; sample=${teams.take(5).joinToString { it.name }}",
            )
        }
        return teams.map { Team(it.id, it.name, it.logoUrl) }
    }

    override fun selectedTeamIds(): Set<Long> = preferences.getStringSet("selected_ids", emptySet())
        .orEmpty().mapNotNull(String::toLongOrNull).toSet()

    override fun saveSelectedTeamIds(ids: Set<Long>) {
        preferences.edit().putStringSet("selected_ids", ids.map(Long::toString).toSet()).apply()
    }
}
