package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
) : MatchRepository {

    private val matchesByDateCache = mutableMapOf<LocalDate, List<Match>>()

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        matchesByDateCache[date]?.let {
            Log.d("MatchCache", "CACHE HIT  for $date")
            return it
        }

        Log.d("MatchCache", "API REQUEST for $date")
        val matches = api.gamesOn(date.toString()).data.map { it.toMatch() }
        matchesByDateCache[date] = matches
        return matches
    }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}