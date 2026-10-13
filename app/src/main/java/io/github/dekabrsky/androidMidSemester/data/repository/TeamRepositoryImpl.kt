package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.loadSStatsPages
import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

private const val RUSSIA_CODE = "RU"

class TeamRepositoryImpl(
    private val api: SStatsApi,
) : TeamRepository {

    private val selected = MutableStateFlow<Set<Long>>(emptySet())

    override suspend fun russianTeams(): List<Team> =
        loadSStatsPages(idOf = { it.id }) { offset, limit ->
            api.teams(country = RUSSIA_CODE, offset = offset, limit = limit)
        }.map { it.toTeam() }
            .filter { it.countryCode == RUSSIA_CODE }
            .sortedBy { it.name.lowercase(Locale.ROOT) }

    override fun selectedTeamIds(): Set<Long> = selected.value
    override fun setSelectedTeamIds(ids: Set<Long>) { selected.value = ids }
    override fun selectedTeamIdsFlow(): StateFlow<Set<Long>> = selected
}