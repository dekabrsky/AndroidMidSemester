package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchMemoryCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: SStatsApi,
    private val cache: MatchMemoryCache,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> {
        // Проверяем наличие списка матчей в in-memory кэше
        cache.get(date)?.let { return it }

        // Если в кэше нет — загружаем из сети и сохраняем
        val matches = api.gamesOn(date.toString()).data.map { it.toMatch() }
        cache.put(date, matches)
        return matches
    }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
