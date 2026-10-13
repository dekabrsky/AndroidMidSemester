package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.data.api.espn.EspnApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "EspnMatchRepository"
private val ESPN_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd")

class EspnMatchRepositoryImpl(
    private val api: EspnApi,
) : MatchRepository {

    private val matchesCache = mutableMapOf<LocalDate, List<Match>>()
    private val cacheMutex = Mutex()

    override suspend fun matchesOn(date: LocalDate): List<Match> = cacheMutex.withLock {
        matchesCache[date]?.let { cached ->
            Log.d(TAG, "CACHE HIT   | ESPN | $date | ${cached.size} матчей")
            return@withLock cached
        }

        Log.d(TAG, "NETWORK REQ | ESPN | $date")
        val response = api.scoreboard(date.format(ESPN_DATE_FORMAT))
        val matches = response.events.mapNotNull { it.toMatch() }

        matchesCache[date] = matches
        Log.d(TAG, "CACHE PUT   | ESPN | $date | ${matches.size} матчей")
        matches
    }

    override suspend fun match(id: Long): Match =
        throw UnsupportedOperationException("Детали по id пока не реализованы в ESPN-репозитории")
    override suspend fun teamsByCountry(country: String): List<Team> =
        throw UnsupportedOperationException("Команды грузятся через sstats")
}