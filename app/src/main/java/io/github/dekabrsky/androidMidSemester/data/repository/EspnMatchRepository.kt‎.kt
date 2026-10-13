package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepository(
    private val api: EspnApi,
    private val cache: MatchesCache,
) : MatchRepository {
    override suspend fun matchesOn(date: LocalDate): List<Match> = cache.getOrLoad(date) {
        val scoreboard = api.scoreboard(date = date.format(DateTimeFormatter.BASIC_ISO_DATE))
        val league = scoreboard.leagues.firstOrNull()?.name.orEmpty()
        scoreboard.events.map { it.toMatch(league) }
    }

    override suspend fun match(id: Long): Match {
        val header = api.summary(id = id).header
            ?: throw NoSuchElementException("Матч ESPN $id не найден")
        return header.toMatch()
    }
}