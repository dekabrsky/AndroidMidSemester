package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository

class ToggleTeamSelectionUseCase(
    private val repository: TeamSelectionRepository,
) {
    operator fun invoke(teamId: Long) = repository.toggle(teamId)
}
