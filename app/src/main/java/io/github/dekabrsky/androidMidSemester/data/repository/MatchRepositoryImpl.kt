package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
    private val cache: MatchCache,
) : MatchRepository {

    /** Фильтр по командам делает сервер: параметр Team принимает несколько id через запятую. */
    override suspend fun matchesOn(date: LocalDate, teamIds: Set<Long>): List<Match> =
        cache.get(date, teamIds) ?: api.gamesOn(
            date = date.toString(),
            teamIds = teamIds.takeIf { it.isNotEmpty() }?.sorted()?.joinToString(","),
        ).data.map { it.toMatch() }
            .also { cache.put(date, teamIds, it) }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
