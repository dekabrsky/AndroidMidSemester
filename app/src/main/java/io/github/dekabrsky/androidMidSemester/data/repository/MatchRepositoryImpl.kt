package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "MatchRepo"

class MatchRepositoryImpl(
    private val api: SStatsApi,
    private val scope: CoroutineScope,
) : MatchRepository {

    private val inFlight = ConcurrentHashMap<LocalDate, Deferred<List<Match>>>()

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        val existing = inFlight[date]

        val deferred: Deferred<List<Match>> = when {
            existing == null -> {
                Log.i(TAG, "MISS  $date — идём в сеть")
                inFlight.computeIfAbsent(date) {
                    scope.async {
                        api.gamesOn(date.toString()).data.map { it.toMatch() }
                    }
                }
            }
            existing.isCompleted && !existing.isCancelled -> {
                Log.i(TAG, "HIT   $date — берём из кэша")
                existing
            }
            else -> {
                Log.i(TAG, "INFLIGHT $date — запрос уже летит, ждём его")
                existing
            }
        }

        return try {
            deferred.await()
        } catch (e: Throwable) {
            Log.w(TAG, "FAIL  $date — ${e.message}")
            inFlight.remove(date, deferred)
            throw e
        }
    }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}