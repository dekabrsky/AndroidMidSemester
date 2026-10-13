package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toTeam
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TeamRepositoryImpl(private val api: SStatsApi) : TeamRepository {
    private var cachedTeams: List<Team>? = null
    private val selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    override val selectedTeamIds = selectedIds.asStateFlow()

    override suspend fun teams(): List<Team> {
        cachedTeams?.let { return it }
        val teams = mutableListOf<Team>()
        do {
            val page = api.teams("Russia", teams.size).data
            teams.addAll(page.map { it.toTeam() })
        } while (page.size == 1000)
        return teams.sortedBy { it.name }.also { cachedTeams = it }
    }

    override fun selectTeams(ids: Set<Long>) {
        selectedIds.value = ids.toSet()
    }
}
