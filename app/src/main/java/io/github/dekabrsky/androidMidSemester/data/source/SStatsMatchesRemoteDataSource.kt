package io.github.dekabrsky.androidMidSemester.data.source

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.loadSStatsPages
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

class SStatsMatchesRemoteDataSource(
    private val api: SStatsApi,
) : MatchesRemoteDataSource {
    override suspend fun matchesOn(date: LocalDate): List<Match> =
        loadSStatsPages(idOf = { it.id }) { offset, limit ->
            api.gamesOn(date.toString(), offset, limit)
        }.map { it.toMatch() }.sortedBy { it.kickoff }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}