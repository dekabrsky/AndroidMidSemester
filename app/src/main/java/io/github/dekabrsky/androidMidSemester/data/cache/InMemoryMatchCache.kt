package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

/** Shared in-memory cache policy. Each data source owns a separate instance. */
class InMemoryMatchCache {
    private val mutex = Mutex()
    private val matchesByDate = mutableMapOf<LocalDate, List<Match>>()
    private val matchesById = mutableMapOf<Long, Match>()

    suspend fun matchesOn(date: LocalDate, load: suspend () -> List<Match>): List<Match> =
        mutex.withLock {
            matchesByDate[date] ?: load().also { matches ->
                matchesByDate[date] = matches
                matches.forEach { match -> matchesById[match.id] = match }
            }
        }

    suspend fun match(id: Long, load: suspend () -> Match): Match = mutex.withLock {
        matchesById[id] ?: load().also { matchesById[id] = it }
    }
}
