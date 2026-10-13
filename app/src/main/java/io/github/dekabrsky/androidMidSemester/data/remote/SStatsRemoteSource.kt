package io.github.dekabrsky.androidMidSemester.data.remote

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

class SStatsRemoteSource(
    val api: SStatsApi
) : RemoteSource {
    override suspend fun matchesOn(date: LocalDate): List<Match> =
        api.gamesOn(date.toString()).data.map { it.toMatch() }

    override suspend fun match(id: Long): Match =
        api.game(id).data?.toMatch() ?: throw NoSuchElementException("Матч $id не найден")
}