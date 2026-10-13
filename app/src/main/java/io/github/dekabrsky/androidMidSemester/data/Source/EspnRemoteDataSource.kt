package io.github.dekabrsky.androidMidSemester.data.source

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatches
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val LEAGUE = "rus.1"

class EspnRemoteDataSource(
    private val api: EspnApi,
) : MatchesRemoteDataSource {
    override suspend fun matchesOn(date: LocalDate): List<Match> =
        api.scoreboard(LEAGUE, date.format(DateTimeFormatter.BASIC_ISO_DATE)).toMatches()

    override suspend fun match(id: Long): Match? =
        api.summary(LEAGUE, id).toMatch()
}