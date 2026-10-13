package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.SelectedTeamsRepository

class ClearTeamSelectionUseCase(private val repository: SelectedTeamsRepository) {
    operator fun invoke() = repository.clear()
}
