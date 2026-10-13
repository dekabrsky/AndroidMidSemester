package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import java.time.LocalDate

interface MatchRepository {
    /** @param teamIds матчи только этих команд; пустое множество означает "все матчи дня". */
    suspend fun matchesOn(date: LocalDate, teamIds: Set<Long> = emptySet()): List<Match>
    suspend fun match(id: Long): Match
}
