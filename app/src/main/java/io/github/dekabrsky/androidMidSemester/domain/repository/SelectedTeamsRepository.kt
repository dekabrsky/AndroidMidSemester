package io.github.dekabrsky.androidMidSemester.domain.repository

import kotlinx.coroutines.flow.Flow

interface SelectedTeamsRepository {
    val selectedTeamIds: Flow<Set<Long>>

    fun toggle(teamId: Long)

    fun clear()
}