package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.source.MatchesRemoteDataSource
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class MatchRepositoryImpl(
    private val remote: MatchesRemoteDataSource,
) : MatchRepository {

    override suspend fun matchesOn(date: LocalDate): List<Match> = remote.matchesOn(date)

    override suspend fun match(id: Long): Match = remote.match(id)
}