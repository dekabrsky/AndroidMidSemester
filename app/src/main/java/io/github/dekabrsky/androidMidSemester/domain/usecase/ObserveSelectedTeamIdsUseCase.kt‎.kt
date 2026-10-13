package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveSelectedTeamIdsUseCase(private val repository: TeamSelectionRepository) {
    operator fun invoke(): StateFlow<Set<Long>> = repository.selectedTeamIds
}