package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatches
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

class EspnMatchRepository(
    private val api: EspnApi,
    private val cache: MatchesCache
) : MatchRepository {

    private val loaded = ConcurrentHashMap<Long, Match>()

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        cache.getOrLoad(date) {
            api.scoreboard(date.format(DateTimeFormatter.BASIC_ISO_DATE))
                .toMatches()
                .also { list -> list.forEach { loaded[it.id] = it } }
        }

    override suspend fun match(id: Long): Match =
        loaded[id] ?: throw NoSuchElementException("Матч $id")
}