package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import java.time.LocalDate

class GetMatchesUseCase(private val repository: MatchRepository) {
    suspend operator fun invoke(date: LocalDate, selectedTeamIds: Set<Long> = emptySet()): List<Match> {
        val matches = repository.matchesOn(date)
        return if (selectedTeamIds.isEmpty()) matches else matches.filter {
            it.home.id in selectedTeamIds || it.away.id in selectedTeamIds
        }
    }
}
