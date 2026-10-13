package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.SelectedTeamsRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveSelectedTeamsUseCase(private val repository: SelectedTeamsRepository) {
    operator fun invoke(): StateFlow<Set<Long>> = repository.selectedTeamIds
}

class SaveSelectedTeamsUseCase(private val repository: SelectedTeamsRepository) {
    operator fun invoke(ids: Set<Long>) = repository.setSelectedTeamIds(ids)
}
