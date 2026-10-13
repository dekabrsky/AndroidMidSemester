package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team

interface TeamsRepository {
    suspend fun teamsIn(country: String): List<Team>
}