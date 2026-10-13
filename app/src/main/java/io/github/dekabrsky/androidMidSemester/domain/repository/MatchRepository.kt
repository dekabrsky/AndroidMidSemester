package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

interface MatchRepository {
    suspend fun matchesOn(date: LocalDate, teamIds: Set<Long> = emptySet()): List<Match>

    suspend fun match(id: Long): Match
}
