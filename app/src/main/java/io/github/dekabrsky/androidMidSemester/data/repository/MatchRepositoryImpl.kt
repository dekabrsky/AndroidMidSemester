package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.loadAllPages
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
    private val cache: MatchesMemoryCache,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        cache.get(date)?.let { return it }

        val matches = loadAllPages { offset, limit ->
            api.gamesOn(date.toString(), offset, limit).data
        }
            .distinctBy { it.id }
            .map { it.toMatch() }
        cache.put(date, matches)
        return matches
    }

    override suspend fun match(id: Long): Match =
        cache.findMatch(id)
            ?: api.game(id).data?.toMatch()
            ?: throw NoSuchElementException("Матч $id не найден")
}
