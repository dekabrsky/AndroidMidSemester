
package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SStatsTeamSelectionRepository(
    private val api: SStatsApi
) : TeamSelectionRepository {

    private val _selectedTeamIds = MutableStateFlow<Set<Long>>(emptySet())
    override val selectedTeamIds = _selectedTeamIds.asStateFlow()

    private val cachedTeams = mutableMapOf<String, List<Team>>()

    override suspend fun getTeamsByCountry(country: String): List<Team> {
        cachedTeams[country]?.let { return it }

        val result = mutableListOf<Team>()
        val seenIds = mutableSetOf<Long>()
        val limit = 100
        var offset = 0

        while (true) {
            val page = api.teams(country, offset, limit).data

            val newTeams = page.filter { seenIds.add(it.id) }
                .map {
                    Team(
                        id = it.id,
                        name = it.name,
                        logoUrl = "https://sstats.net/assets/logos/${it.id}.png"
                    )
                }

            result.addAll(newTeams)

            if (page.size < limit || newTeams.isEmpty()) break
            offset += limit
        }

        return result.sortedBy { it.name }
            .also { cachedTeams[country] = it }
    }

    override fun updateSelection(teamId: Long, selected: Boolean) {
        _selectedTeamIds.update { current ->
            if (selected) current + teamId
            else current - teamId
        }
    }
}
