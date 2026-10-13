package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val LOGO_URL_PATTERN = "https://sstats.net/assets/logos/%d.png"

class TeamRepositoryImpl(
    private val api: SStatsApi,
) : TeamRepository {

    private val _selectedTeamIds = MutableStateFlow<Set<Long>>(emptySet())
    override fun getSelectedTeamIds(): StateFlow<Set<Long>> = _selectedTeamIds.asStateFlow()

    override suspend fun getTeams(country: String): List<Team> {
        return api.teamsByCountry(country).data.map { dto ->
            Team(
                id = dto.id,
                name = dto.name,
                logoUrl = dto.logoUrl ?: LOGO_URL_PATTERN.format(dto.id),
            )
        }
    }

    override fun toggleTeamSelection(teamId: Long) {
        val current = _selectedTeamIds.value
        _selectedTeamIds.value = if (teamId in current) {
            current - teamId
        } else {
            current + teamId
        }
    }

    override fun clearSelection() {
        _selectedTeamIds.value = emptySet()
    }
}
