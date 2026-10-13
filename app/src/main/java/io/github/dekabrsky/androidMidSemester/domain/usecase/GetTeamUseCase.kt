package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository

class GetTeamsUseCase(private val repository: MatchRepository) {
    suspend operator fun invoke(country: String): List<Team> {
        return repository.teamsByCountry(country)
    }
}