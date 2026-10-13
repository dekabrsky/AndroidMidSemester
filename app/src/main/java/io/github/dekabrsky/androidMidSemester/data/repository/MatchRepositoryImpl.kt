package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val remote: MatchesRemoteDataSource,
    private val cache: MatchesCache,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        cache.get(date)
            ?: remote.matchesOn(date)
                .also { matches -> cache.put(date, matches) }

    override suspend fun match(id: Long): Match =
        remote.match(id) ?: throw NoSuchElementException("Матч $id не найден")
}