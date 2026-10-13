package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

class MatchCache {
    private val matchesByDate = mutableMapOf<LocalDate, List<Match>>()
    private val mutex = Mutex()

    suspend fun getOrLoad(
        date: LocalDate,
        load: suspend () -> List<Match>,
    ): List<Match> = mutex.withLock {
        matchesByDate[date] ?: load().also { matchesByDate[date] = it }
    }
}
