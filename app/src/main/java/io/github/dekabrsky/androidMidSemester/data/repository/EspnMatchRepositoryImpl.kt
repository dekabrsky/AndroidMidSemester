package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.toMatch
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesInMemoryCache
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class EspnMatchRepositoryImpl(
    private val api: EspnApi,
    private val cache: MatchesInMemoryCache,
) : MatchRepository {

    private val TAG = "EspnMatchRepository"

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        val cachedMatches = cache.get(date)
        if (cachedMatches != null) {
            Log.d(TAG, "✅ CACHE HIT (ESPN): Загружено ${cachedMatches.size} матчей на $date из памяти")
            return cachedMatches
        }

        Log.d(TAG, "❌ CACHE MISS (ESPN): Запрос к API ESPN для даты $date")
        // ESPN требует формат даты YYYYMMDD (без дефисов)
        val espnDate = date.toString().replace("-", "")
        val response = api.getScoreboard(espnDate)

        val leagueName = response.leagues.firstOrNull()?.name ?: "Russian Premier League"
        val matches = response.events.map { it.toMatch(leagueName) }

        cache.put(date, matches)
        Log.d(TAG, "💾 CACHE SAVED (ESPN): Сохранено ${matches.size} матчей в кэш")

        return matches
    }

    override suspend fun match(id: Long): Match {
        Log.d(TAG, "🔍 Запрос деталей матча $id к ESPN API")
        val response = api.getSummary(id.toString())
        return response.toMatch()
    }

    override suspend fun teamsByCountry(country: String): List<Team> {
        TODO("Not yet implemented")
    }
}