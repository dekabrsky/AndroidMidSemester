package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.MatchCache
import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatchOrNull
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepository(
    private val api: EspnApi,
    private val cache: MatchCache,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        cache.get(date)?.let { cachedMatches ->
            return cachedMatches
        }

        val formattedDate = date.format(DateTimeFormatter.BASIC_ISO_DATE)

        val matches = api.gamesOn(formattedDate)
            .events
            .mapNotNull { it.toMatchOrNull() }

        cache.put(date, matches)

        return matches
    }

    override suspend fun match(id: Long): Match {
        throw UnsupportedOperationException(
            "Получение отдельного матча по ID через ESPN пока не реализовано"
        )
    }
}