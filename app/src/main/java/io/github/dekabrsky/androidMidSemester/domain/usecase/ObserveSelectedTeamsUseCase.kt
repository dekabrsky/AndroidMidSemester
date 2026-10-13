package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveSelectedTeamsUseCase(private val repository: TeamRepository) {
    operator fun invoke(): StateFlow<Set<Long>> = repository.selectedTeamIds
}
