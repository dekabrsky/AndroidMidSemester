package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.InMemoryMatchCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toEspnMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepositoryImpl(
    private val api: EspnApi,
) : MatchRepository {

    private val cache = InMemoryMatchCache()

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        cache.matchesOn(date) {
            api.scoreboard(date.format(ESPN_DATE_FORMAT)).let { response ->
                val league = response.leagues.firstOrNull()?.name.orEmpty()
                response.events.map { it.toEspnMatch(league) }
            }
        }

    override suspend fun match(id: Long): Match = cache.match(id) {
        api.summary(id.toString()).header?.let { event ->
            event.toEspnMatch(leagueName = "Russian Premier League")
        } ?: throw NoSuchElementException("Матч $id не найден")
    }

    private companion object {
        val ESPN_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE
    }
}
