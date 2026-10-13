package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

private const val COUNTRY = "Russia"

class GetTeamsUseCase(private val repository: TeamRepository) {
    suspend operator fun invoke(): List<Team> = repository.teamsOf(COUNTRY)
}
