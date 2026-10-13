package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepositoryImpl(
    private val api: EspnApi,
    private val cache: MatchCache,
) : MatchRepository {
    override suspend fun matchesOn(date: LocalDate): List<Match> =
        cache.getOrLoad(date) {
            val scoreboard = api.scoreboard(date.format(DateTimeFormatter.BASIC_ISO_DATE))
            val leagueName = scoreboard.leagues.firstOrNull()?.name.orEmpty()
            scoreboard.events.map { it.toMatch(leagueName) }
        }

    override suspend fun match(id: Long): Match {
        val event = api.summary(id).header
            ?: throw NoSuchElementException("Матч $id не найден")
        return event.toMatch(event.league?.name.orEmpty())
    }
}
