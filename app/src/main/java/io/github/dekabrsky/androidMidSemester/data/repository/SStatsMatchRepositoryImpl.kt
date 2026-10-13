package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.loadAllPages
import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import java.time.LocalDate

class SStatsMatchRepositoryImpl(
    private val api: SStatsApi,
    private val cache: MatchCache,
    private val teamRepository: TeamRepository,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        cache.getOrLoad(date) {
            val teamIds = teamRepository.teamsIn("Russia").joinToString(",") { it.id.toString() }
            if (teamIds.isEmpty()) {
                emptyList()
            } else {
                loadAllPages(pageSize = 5) { offset, limit ->
                    api.gamesOn(date.toString(), offset, limit, teamIds)
                }.map { it.toMatch() }
            }
        }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
