package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match

class FilterMatchesUseCase {
    operator fun invoke(
        matches: List<Match>,
        selectedTeamIds: Set<Long>,
    ): List<Match> =
        if (selectedTeamIds.isEmpty()) matches
        else matches.filter { it.home.id in selectedTeamIds || it.away.id in selectedTeamIds }
            .distinctBy { it.id }
}