package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

/** Требование бизнеса: выбор делаем из команд России. */
const val DEFAULT_TEAMS_COUNTRY = "Russia"

class GetTeamsUseCase(private val repository: TeamRepository) {
    suspend operator fun invoke(country: String = DEFAULT_TEAMS_COUNTRY): List<Team> =
        repository.teamsOf(country)
}
