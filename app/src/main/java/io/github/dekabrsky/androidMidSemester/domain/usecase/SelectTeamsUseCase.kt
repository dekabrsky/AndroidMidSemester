package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

class SelectTeamsUseCase(private val repository: TeamRepository) {
    operator fun invoke(teamIds: Set<Long>) = repository.selectTeams(teamIds)
}
