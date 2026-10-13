package io.github.dekabrsky.androidMidSemester.data.repository

import android.util.Log
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

private const val TAG = "MatchRepository"

class MatchRepositoryImpl(
    private val remote: MatchesRemoteDataSource,
    private val cache: MatchesMemoryCache,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate, teamIds: Set<Long>): List<Match> =
        cache.getOrLoad(date, teamIds) {
            Log.d(TAG, "Кэш-промах для $date, команды $teamIds — запрос в сеть (${remote::class.simpleName})")
            remote.matchesOn(date, teamIds)
        }.also { Log.d(TAG, "Матчи на $date, команды $teamIds: ${it.size} шт.") }

    override suspend fun match(id: Long): Match = remote.match(id)
}
