
package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MatchRepositoryImpl(
    private val api: EspnApi,
) : MatchRepository {

    private val cache = MatchesMemoryCache()

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        cache.get(date)?.let { matches ->
            Log.d("MATCH_CACHE", "CACHE: $date")
            return matches
        }

        Log.d("MATCH_CACHE", "API ESPN: $date")

        val formattedDate = date.format(
            DateTimeFormatter.BASIC_ISO_DATE
        )

        val response = api.gamesOn(formattedDate)

        val league = response.leagues.firstOrNull()?.name
            ?: "Russian Premier League"

        val matches = response.events.map {
            it.toMatch(league)
        }

        cache.put(date, matches)

        return matches
    }

    override suspend fun match(id: Long): Match {
        return api.game(id).toMatch(id)
    }
}
