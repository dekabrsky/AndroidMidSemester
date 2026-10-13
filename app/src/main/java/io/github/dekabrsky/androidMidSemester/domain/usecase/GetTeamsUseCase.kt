package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

const val TEAMS_COUNTRY = "Russia"

class GetTeamsUseCase(private val repository: TeamRepository) {
    suspend operator fun invoke(country: String = TEAMS_COUNTRY): List<Team> =
        repository.teamsOf(country).sortedBy { it.name }
}
