package io.github.dekabrsky.androidMidSemester.data.source

import android.util.Log
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "MatchSource"

class CachingMatchesRemoteDataSource(
    private val delegate: MatchesRemoteDataSource,
    private val name: String,
    private val scope: CoroutineScope,
) : MatchesRemoteDataSource {

    private val inFlight = ConcurrentHashMap<LocalDate, Deferred<List<Match>>>()

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        val existing = inFlight[date]
        val deferred = when {
            existing == null -> {
                Log.d(TAG, "[$name] MISS     $date — сеть")
                inFlight.computeIfAbsent(date) {
                    scope.async { delegate.matchesOn(date) }
                }
            }
            existing.isCompleted && !existing.isCancelled -> {
                Log.d(TAG, "[$name] HIT      $date — кэш")
                existing
            }
            else -> {
                Log.d(TAG, "[$name] INFLIGHT $date — ждём")
                existing
            }
        }
        return try {
            deferred.await()
        } catch (e: Throwable) {
            Log.w(TAG, "[$name] FAIL     $date — ${e.message}")
            inFlight.remove(date, deferred)
            throw e
        }
    }

    override suspend fun match(id: Long): Match = delegate.match(id)
}