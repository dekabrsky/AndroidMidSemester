package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

class GetRussianTeamsUseCase(private val repository: TeamRepository) {
    suspend operator fun invoke(): List<Team> = repository.russianTeams()
}
