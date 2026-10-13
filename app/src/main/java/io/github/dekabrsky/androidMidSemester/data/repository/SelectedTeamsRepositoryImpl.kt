package io.github.dekabrsky.androidMidSemester.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.dekabrsky.androidMidSemester.domain.repository.SelectedTeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val KEY_SELECTED_TEAMS = "selected_team_ids"

class SelectedTeamsRepositoryImpl(
    private val prefs: SharedPreferences,
) : SelectedTeamsRepository {

    private val _selectedTeamIds = MutableStateFlow(readFromPrefs())
    override val selectedTeamIds: StateFlow<Set<Long>> = _selectedTeamIds.asStateFlow()

    override fun setSelectedTeamIds(ids: Set<Long>) {
        prefs.edit { putStringSet(KEY_SELECTED_TEAMS, ids.map(Long::toString).toSet()) }
        _selectedTeamIds.value = ids
    }

    private fun readFromPrefs(): Set<Long> =
        prefs.getStringSet(KEY_SELECTED_TEAMS, emptySet())
            .orEmpty()
            .mapNotNull(String::toLongOrNull)
            .toSet()
}
