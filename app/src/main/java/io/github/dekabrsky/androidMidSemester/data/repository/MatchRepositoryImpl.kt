package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.ZoneId

class MatchRepositoryImpl(
    private val api: SStatsApi,
    private val cache: MatchCache,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        cache.get(date)?.let { return it }
        val matches = mutableListOf<Match>()
        val timeZone = date.atStartOfDay(ZoneId.systemDefault()).offset.totalSeconds / 3600
        do {
            val page = api.gamesOn(date.toString(), matches.size, timeZone).data
            matches.addAll(page.map { it.toMatch() })
        } while (page.size == 1000)
        cache.put(date, matches)
        return matches
    }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
