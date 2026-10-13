package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchCache
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EspnMatchRepositoryImpl(
    private val api: EspnApi,
    private val cache: MatchCache,
) : MatchRepository {
    /**
     * ESPN не умеет фильтровать по командам, поэтому фильтруем на клиенте.
     * Важно: id команд ESPN не совпадают с id sstats, так что фильтр из экрана
     * выбора команд (он грузит команды sstats) с этим источником не сработает.
     * Сейчас реализация не подключена в DI и оставлена для истории (итерация 3).
     */
    override suspend fun matchesOn(date: LocalDate, teamIds: Set<Long>): List<Match> =
        (cache.get(date, emptySet()) ?: api.scoreboard(date.format(DateTimeFormatter.BASIC_ISO_DATE))
            .let { response ->
                response.events.map { it.toMatch(response.leagues.firstOrNull()?.name.orEmpty()) }
            }.also { cache.put(date, emptySet(), it) })
            .filter { teamIds.isEmpty() || it.home.id in teamIds || it.away.id in teamIds }

    override suspend fun match(id: Long): Match =
        api.summary(id).header.let { it.toMatch(it.league?.name.orEmpty()) }
}
