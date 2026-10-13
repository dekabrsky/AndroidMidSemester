package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TeamRepositoryImpl(
    private val api: SStatsApi,
) : TeamRepository {

    private val _selectedTeamIds = MutableStateFlow<Set<Long>>(emptySet())
    override val selectedTeamIds: StateFlow<Set<Long>> = _selectedTeamIds.asStateFlow()

    private var cachedTeams: List<Team>? = null

    override suspend fun getTeams(country: String): List<Team> {
        cachedTeams?.let { return it }

        val response = api.teams(country = country)
        val teams = response.data.mapNotNull { dto ->
            dto.id?.let { id ->
                Team(
                    id = id,
                    name = dto.name.orEmpty(),
                    logoUrl = dto.logoUrl ?: dto.logo ?: dto.badge ?: "https://sstats.net/assets/logos/$id.png",
                )
            }
        }

        cachedTeams = teams
        return teams
    }

    override fun toggleTeamSelection(teamId: Long) {
        _selectedTeamIds.update { current ->
            if (current.contains(teamId)) current - teamId else current + teamId
        }
    }

    override fun clearSelection() {
        _selectedTeamIds.value = emptySet()
    }

    override fun getSelectedTeamNames(): Set<String> {
        val selected = _selectedTeamIds.value
        if (selected.isEmpty()) return emptySet()
        return cachedTeams
            ?.filter { selected.contains(it.id) }
            ?.map { it.name.lowercase() }
            ?.toSet() ?: emptySet()
    }
}