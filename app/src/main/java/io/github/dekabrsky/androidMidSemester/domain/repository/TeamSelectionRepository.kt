
package io.github.dekabrsky.androidMidSemester.domain.repository

import io.github.dekabrsky.androidMidSemester.domain.model.Team
import kotlinx.coroutines.flow.StateFlow

interface TeamSelectionRepository {

    val selectedTeamIds: StateFlow<Set<Long>>

    suspend fun getTeamsByCountry(country: String): List<Team>

    fun updateSelection(teamId: Long, selected: Boolean)
}
