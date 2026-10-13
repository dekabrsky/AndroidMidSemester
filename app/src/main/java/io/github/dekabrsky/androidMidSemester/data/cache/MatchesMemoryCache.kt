package io.github.dekabrsky.androidMidSemester.data.cache

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

/** Хранит матчи посещённых дат до завершения процесса приложения. */
class MatchesMemoryCache {
    private val matchesByDate = mutableMapOf<LocalDate, List<Match>>()
    private val mutex = Mutex()

    suspend fun getOrPut(
        date: LocalDate,
        load: suspend () -> List<Match>,
    ): List<Match> = mutex.withLock {
        matchesByDate[date] ?: load().also { matches ->
            matchesByDate[date] = matches
        }
    }

    suspend fun findMatch(id: Long): Match? = mutex.withLock {
        matchesByDate.values.asSequence().flatten().firstOrNull { it.id == id }
    }
}
