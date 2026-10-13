package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.espn.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepository(
    private val api: EspnApi,
    private val cache: MatchesMemoryCache,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> = cache.getOrPut(date) {
        val response = api.scoreboard(date.format(DateTimeFormatter.BASIC_ISO_DATE))
        val leagueName = response.leagues.firstOrNull()?.name ?: DEFAULT_LEAGUE_NAME
        response.events.mapNotNull { event -> event.toMatch(leagueName) }
    }

    override suspend fun match(id: Long): Match = cache.findMatch(id)
        ?: api.summary(id.toString()).header.toMatch()
        ?: throw NoSuchElementException("Матч $id не найден")

    private companion object {
        const val DEFAULT_LEAGUE_NAME = "Russian Premier League"
    }
}
