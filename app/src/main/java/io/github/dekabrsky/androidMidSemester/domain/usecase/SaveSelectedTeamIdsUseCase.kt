package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository

class SaveSelectedTeamIdsUseCase(private val repository: TeamSelectionRepository) {
    operator fun invoke(ids: Set<Long>) = repository.setSelectedTeamIds(ids)
}
