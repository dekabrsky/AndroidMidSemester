package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource

private const val TAG = "MATCH_CACHE"

class MatchRepositoryImpl(
    private val remoteSource: MatchesRemoteDataSource,
    private val cache: MatchesCache
) : MatchRepository {

    private val loadMutex = Mutex()

    override suspend fun matchesOn(date: LocalDate): List<Match>
    {
        return loadMutex.withLock {
            val cached = cache.get(date)
            if (cached != null) {
                Log.d(TAG, "Значение взято из кэша")
                return@withLock cached
            }

            val matches = remoteSource.matchesOn(date)
            cache.put(date, matches)
            Log.d(TAG, "Значение взято из API")
            matches
        }
    }


    override suspend fun match(id: Long): Match =
        remoteSource.match(id)
}
