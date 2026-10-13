package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

class MatchesMemoryCache {

    private data class Key(val date: LocalDate, val teamIds: Set<Long>)

    private val mutex = Mutex()
    private val matches = mutableMapOf<Key, List<Match>>()

    suspend fun getOrLoad(
        date: LocalDate,
        teamIds: Set<Long>,
        load: suspend () -> List<Match>,
    ): List<Match> = mutex.withLock {
        val key = Key(date, teamIds)
        matches[key] ?: load().also { matches[key] = it }
    }

    suspend fun clear() = mutex.withLock { matches.clear() }
}
