package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

class GetTeamsUseCase(
    private val repository: TeamRepository,
) {
    suspend operator fun invoke(country: String): List<Team> = repository.teams(country)
}
