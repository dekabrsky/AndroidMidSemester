package io.github.dekabrsky.androidMidSemester.data.source

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatches
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val ESPN_LEAGUE = "rus.1"
private val EspnDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

class EspnRemoteDataSource(
    private val api: EspnApi,
) : MatchesRemoteDataSource {

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        api.scoreboard(
            league = ESPN_LEAGUE,
            dates = date.format(EspnDateFormatter),
        ).toMatches()

    override suspend fun match(id: Long): Match =
        throw NotImplementedError(
            "ESPN summary endpoint не подключён: детали приходят из списка"
        )
}