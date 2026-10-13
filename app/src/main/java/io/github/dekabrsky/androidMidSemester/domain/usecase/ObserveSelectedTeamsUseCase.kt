package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow

class ObserveSelectedTeamsUseCase(private val repository: TeamRepository) {
    operator fun invoke(): Flow<Set<Long>> = repository.selectedTeamIds
}
