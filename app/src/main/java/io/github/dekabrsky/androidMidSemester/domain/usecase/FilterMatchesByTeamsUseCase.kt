package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match

class FilterMatchesByTeamsUseCase {
    operator fun invoke(matches: List<Match>, teamIds: Set<Long>): List<Match> =
        if (teamIds.isEmpty()) matches else matches.filter {
            it.home.id in teamIds || it.away.id in teamIds
        }
}
