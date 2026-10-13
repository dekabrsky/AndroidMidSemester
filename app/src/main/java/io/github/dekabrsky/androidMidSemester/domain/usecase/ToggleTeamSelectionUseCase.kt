package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.SelectedTeamsRepository

class ToggleTeamSelectionUseCase(private val repository: SelectedTeamsRepository) {
    operator fun invoke(teamId: Long) = repository.toggle(teamId)
}
