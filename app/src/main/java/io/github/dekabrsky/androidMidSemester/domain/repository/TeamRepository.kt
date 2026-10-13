package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team

interface TeamRepository {
    suspend fun teamsInCountry(code: String): List<Team>
}