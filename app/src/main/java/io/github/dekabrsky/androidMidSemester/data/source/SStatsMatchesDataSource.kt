package io.github.dekabrsky.androidMidSemester.data.source

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

class SStatsMatchesDataSource(
    private val api: SStatsApi,
) : MatchesRemoteDataSource {

    override suspend fun matchesOn(date: LocalDate, teamIds: Set<Long>): List<Match> =
        api.gamesOn(
            date = date.toString(),
            team = teamIds.takeIf { it.isNotEmpty() }?.sorted()?.joinToString(","),
        ).data.map { it.toMatch() }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
