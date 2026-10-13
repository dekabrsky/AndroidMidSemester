package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository

class ToggleTeamSelectionUseCase(private val repository: TeamRepository) {
    operator fun invoke(teamId: Long) = repository.toggleTeam(teamId)
}
