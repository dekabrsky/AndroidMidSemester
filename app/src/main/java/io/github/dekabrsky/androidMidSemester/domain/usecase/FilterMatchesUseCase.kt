package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match

class FilterMatchesUseCase {
    operator fun invoke(
        matches: List<Match>,
        selectedTeamIds: Set<Long>,
        country: String? = null,
    ): List<Match> = matches.filter {
        (selectedTeamIds.isEmpty() && country == null) ||
            it.home.id in selectedTeamIds || it.away.id in selectedTeamIds ||
            (country != null && (it.home.country == country || it.away.country == country))
    }.distinctBy { it.id }
}
