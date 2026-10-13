package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TeamRepositoryImpl(private val api: SStatsApi) : TeamRepository {
    private val teamsByCountry = mutableMapOf<String, List<Team>>()
    private val mutex = Mutex()

    override suspend fun teamsFrom(country: String): List<Team> = mutex.withLock {
        teamsByCountry[country] ?: loadTeams(country).also { teamsByCountry[country] = it }
    }

    private suspend fun loadTeams(country: String): List<Team> {
        val teams = mutableListOf<Team>()
        var offset = 0
        do {
            val page = api.teams(country = country, offset = offset, limit = PAGE_SIZE)
            check(page.status == "OK") { "Не удалось загрузить команды" }
            teams += page.data.map { it.toTeam() }
            offset += page.data.size
        } while (page.data.isNotEmpty() &&
            (page.totalCount?.let { offset < it } ?: (page.data.size == PAGE_SIZE)))
        return teams.distinctBy { it.id }.sortedBy { it.name.lowercase() }
    }

    private companion object {
        const val PAGE_SIZE = 1000
    }
}