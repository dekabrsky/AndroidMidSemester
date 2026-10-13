package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepositoryImpl(
    private val api: EspnApi,
    private val cache: MatchesMemoryCache,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        cache.get(date)?.let { return it }

        val response = api.scoreboard(date.format(DateTimeFormatter.BASIC_ISO_DATE))
        val league = response.leagues.firstOrNull()?.name.orEmpty()
        val matches = response.events.map { it.toMatch(league) }
        cache.put(date, matches)
        return matches
    }

    override suspend fun match(id: Long): Match =
        cache.findMatch(id)
            ?: api.summary(id).toMatch(id)
            ?: throw NoSuchElementException("Матч $id не найден")
}
