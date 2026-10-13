package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
) : MatchRepository {

    private val matchesCache = mutableMapOf<LocalDate, List<Match>>()
    private val cacheMutex = Mutex()

    override suspend fun matchesOn(date: LocalDate): List<Match> = cacheMutex.withLock {
        matchesCache[date] ?: api.gamesOn(date.toString()).data
            .map { it.toMatch() }
            .also { matchesCache[date] = it }
    }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
