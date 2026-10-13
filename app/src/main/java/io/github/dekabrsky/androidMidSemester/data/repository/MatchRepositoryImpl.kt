package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.mapper.toMatch
import io.github.dekabrsky.androidMidSemester.data.remote.RemoteSource
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val api: RemoteSource,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> =
        api.matchesOn(date)

    override suspend fun match(id: Long): Match =
        api.match(id)
}
