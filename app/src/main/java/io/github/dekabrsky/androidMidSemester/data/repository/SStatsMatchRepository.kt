package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

/** Прежняя реализация сохранена для возможного возврата к sstats. */
class SStatsMatchRepository(
    private val api: SStatsApi,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        api.gamesOn(date.toString()).data.map { it.toMatch() }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}
