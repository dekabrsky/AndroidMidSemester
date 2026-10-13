package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository

class GetTeamsByCountryUseCase(private val repository: MatchRepository) {
    suspend operator fun invoke(country: String): List<Team> = repository.teamsByCountry(country)
}