package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.SelectedTeamsRepository
import kotlinx.coroutines.flow.Flow

class ObserveSelectedTeamsUseCase(private val repository: SelectedTeamsRepository) {
    operator fun invoke(): Flow<Set<Long>> = repository.selectedTeamIds
}
