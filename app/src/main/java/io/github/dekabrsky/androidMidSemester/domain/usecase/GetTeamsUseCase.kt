package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

class GetTeamsUseCase(
    private val repository: TeamRepository,
) {
    suspend operator fun invoke(countryCode: String): List<Team> =
        repository.teamsInCountry(countryCode)
}