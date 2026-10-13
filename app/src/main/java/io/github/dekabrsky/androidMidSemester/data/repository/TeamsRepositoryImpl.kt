package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.loadSStatsPages
import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamsRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Collections
import java.util.Locale

class TeamsRepositoryImpl(private val api: SStatsApi) : TeamsRepository {
    private val teamsByCountry = mutableMapOf<String, List<Team>>()
    private val loadMutex = Mutex()

    override suspend fun teamsIn(country: String): List<Team> {
        val countryName = country.trim()
        require(countryName.isNotEmpty()) { "Не указана страна команд" }

        return loadMutex.withLock {
            teamsByCountry[countryName]?.let { return@withLock it }

            val teams = Collections.unmodifiableList(loadTeams(countryName))
            teamsByCountry[countryName] = teams
            teams
        }
    }

    private suspend fun loadTeams(country: String): List<Team> =
        loadSStatsPages(idOf = { it.id }) { offset, limit ->
            api.teams(country = country, offset = offset, limit = limit)
        }.map { it.toTeam() }.sortedBy { it.name.lowercase(Locale.ROOT) }
}
