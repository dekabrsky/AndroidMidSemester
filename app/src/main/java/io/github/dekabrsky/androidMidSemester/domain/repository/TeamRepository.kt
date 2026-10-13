package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team

interface TeamRepository {
    /** @param country название страны на английском, например "Russia". */
    suspend fun teamsOf(country: String): List<Team>
}
