package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class GetMatchesUseCase(private val repository: MatchRepository) {
    suspend operator fun invoke(date: LocalDate, teamIds: Set<Long> = emptySet()): List<Match> =
        repository.matchesOn(date, teamIds)
}
