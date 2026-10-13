package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import java.time.LocalDate

class GetMatchesUseCase(
    private val matchRepository: MatchRepository,
    private val teamRepository: TeamRepository,
) {
    suspend operator fun invoke(date: LocalDate): List<Match> {
        val matches = matchRepository.matchesOn(date)
        val selectedTeamIds = teamRepository.getSelectedTeamIds().value

        if (selectedTeamIds.isEmpty()) {
            return matches
        }

        return matches.filter { match ->
            (match.home.id in selectedTeamIds) || (match.away.id in selectedTeamIds)
        }
    }
}
