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
        cache.get(date) ?: api.scoreboard(date.format(DateTimeFormatter.BASIC_ISO_DATE))
            .let { response ->
                response.events.map { it.toMatch(response.leagues.firstOrNull()?.name.orEmpty()) }
            }.also { cache.put(date, it) }

    override suspend fun match(id: Long): Match =
        api.summary(id).header.let { it.toMatch(it.league?.name.orEmpty()) }
}
