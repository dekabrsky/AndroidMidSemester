package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
    private val cache: MatchesCache,
    private val teams: TeamRepository,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        cache.getOrLoad(date) {
            val russianTeams = teams.teamsFrom("Russia")
            if (russianTeams.isEmpty()) return@getOrLoad emptyList()
            val teamIds = russianTeams.map { it.id }.distinct().sorted().joinToString(",")
            val matches = mutableListOf<Match>()
            var offset = 0
            do {
                val page = api.gamesOn(date.toString(), teamIds, offset, PAGE_SIZE)
                check(page.status == "OK") { "Не удалось загрузить матчи" }
                matches += page.data.map { it.toMatch() }
                offset += page.data.size
            } while (page.data.size == PAGE_SIZE)
            matches.distinctBy { it.id }.sortedBy { it.kickoff }
        }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")

    private companion object {
        const val PAGE_SIZE = 1000
    }
}
