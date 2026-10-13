package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private fun TeamDto.toDomainTeam() = Team(
    id = id,
    name = name,
    logoUrl = "https://sstats.net/assets/logos/$id.png",
)

class SStatsTeamRepositoryImpl(
    private val api: SStatsApi,
) : TeamRepository {

    private val cacheMutex = Mutex()
    private var teamsCache: List<Team>? = null
    private val _selectedTeamIds = MutableStateFlow<Set<Long>>(emptySet())

    override val selectedTeamIds: StateFlow<Set<Long>> = _selectedTeamIds.asStateFlow()

    override suspend fun russianTeams(): List<Team> = cacheMutex.withLock {
        teamsCache ?: api.teamsFromCountry(COUNTRY_RUSSIA).data
            .map(TeamDto::toDomainTeam)
            .also { teamsCache = it }
    }

    override fun updateSelectedTeamIds(ids: Set<Long>) {
        _selectedTeamIds.value = ids
    }

    private companion object {
        const val COUNTRY_RUSSIA = "Russia"
    }
}
