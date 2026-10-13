package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

class MatchesCache {
    private val mutex = Mutex()
    private val storage = mutableMapOf<LocalDate, List<Match>>()

    suspend fun getOrLoad(
        date: LocalDate,
        load: suspend () -> List<Match>
    ): List<Match> = mutex.withLock {
        storage[date] ?: load().also { storage[date] = it }
    }
}