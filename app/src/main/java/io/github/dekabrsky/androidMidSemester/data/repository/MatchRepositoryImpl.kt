package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.data.mapper.toDomain
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

private const val TAG = "MatchRepository"

class MatchRepositoryImpl(
    private val api: SStatsApi,
) : MatchRepository {

    /**
     * Кэш матчей по дате. Живёт весь срок жизни репозитория, а он в Koin —
     * single, значит данные не теряются до закрытия приложения.
     */
    private val matchesCache = mutableMapOf<LocalDate, List<Match>>()

    /** Защита от гонок при быстрых кликах «вперёд-назад-вперёд». */
    private val cacheMutex = Mutex()

    override suspend fun matchesOn(date: LocalDate): List<Match> = cacheMutex.withLock {
        // 1. Кэш-попадание — сразу отдаём, без сети.
        matchesCache[date]?.let { cached ->
            Log.d(TAG, "CACHE HIT   | $date | ${cached.size} матчей")
            return@withLock cached
        }

        // 2. Кэш-промах — идём в сеть.
        Log.d(TAG, "NETWORK REQ | $date")
        val matches = api.gamesOn(date.toString()).data.map { it.toMatch() }

        // 3. Кладём в кэш. Если запрос упадёт — сюда не дойдём, ошибки не кэшируются.
        matchesCache[date] = matches
        Log.d(TAG, "CACHE PUT   | $date | ${matches.size} матчей")

        matches
    }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
    override suspend fun teamsByCountry(country: String): List<Team> =
        api.teams(country).data.map { it.toDomain() }
}