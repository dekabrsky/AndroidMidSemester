package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team

interface TeamRepository {
    suspend fun teamsOf(country: String): List<Team>
}
