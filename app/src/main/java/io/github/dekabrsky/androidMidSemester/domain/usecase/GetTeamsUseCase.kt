package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamsRepository

class GetTeamsUseCase(private val repository: TeamsRepository) {
    suspend operator fun invoke(country: String): List<Team> = repository.teamsIn(country)
}