package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepository(
    private val api: EspnApi,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        val response = api.scoreboard(date.format(DateTimeFormatter.BASIC_ISO_DATE))
        val leagueName = response.leagues.firstOrNull()?.name.orEmpty()
        return response.events.map { it.toMatch(leagueName) }
    }

    override suspend fun match(id: Long): Match =
        api.summary(id).header?.toMatch()
            ?: throw NoSuchElementException("Матч ESPN $id не найден")
}
