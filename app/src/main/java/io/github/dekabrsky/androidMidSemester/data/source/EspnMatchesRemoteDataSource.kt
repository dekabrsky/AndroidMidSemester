package io.github.dekabrsky.androidMidSemester.data.source

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatches
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val SCOREBOARD_LIMIT = 1000

class EspnMatchesRemoteDataSource(
    private val api: EspnApi,
    private val league: String,
) : MatchesRemoteDataSource {
    override suspend fun matchesOn(date: LocalDate): List<Match> =
        api.scoreboard(
            league = league,
            date = date.format(DateTimeFormatter.BASIC_ISO_DATE),
            limit = SCOREBOARD_LIMIT,
        ).toMatches()

    override suspend fun match(id: Long): Match = api.summary(league, id).header.toMatch()
}
