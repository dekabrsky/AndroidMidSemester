
package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
) : MatchRepository {

    private val cache = MatchesMemoryCache()

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        cache.get(date)?.let { matches ->
            Log.d("MATCH_CACHE", "CACHE: $date")
            return matches
        }

        Log.d("MATCH_CACHE", "API: $date")

        val matches = api.gamesOn(date.toString())
            .data.map { it.toMatch() }

        cache.put(date, matches)

        return matches
    }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch()
            ?: throw NoSuchElementException("Матч $id не найден")
}
