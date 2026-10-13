package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

class MatchRepositoryImpl(
    private val remoteSource: MatchesRemoteDataSource,
    private val cache: MatchesMemoryCache,
) : MatchRepository {
    private val loadMutex = Mutex()

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        cache.get(date)?.let { return it }

        return loadMutex.withLock {
            cache.get(date)?.let { return@withLock it }

            val matches = remoteSource.matchesOn(date)
            cache.put(date, matches)
            matches
        }
    }

    override suspend fun match(id: Long): Match =
        remoteSource.match(id)
}
