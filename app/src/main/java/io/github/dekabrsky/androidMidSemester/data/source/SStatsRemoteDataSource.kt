package io.github.dekabrsky.androidMidSemester.data.source

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

class SStatsRemoteDataSource(
    private val api: SStatsApi,
) : MatchesRemoteDataSource {

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        api.gamesOn(date.toString()).data.map { it.toMatch() }

    override suspend fun match(id: Long): Match? =
        api.game(id).data?.toMatch()
}
