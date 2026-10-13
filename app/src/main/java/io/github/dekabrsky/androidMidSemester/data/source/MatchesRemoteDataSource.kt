package io.github.dekabrsky.androidMidSemester.data.source

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

interface MatchesRemoteDataSource {
    suspend fun matchesOn(date: LocalDate, teamIds: Set<Long>): List<Match>
    suspend fun match(id: Long): Match
}
