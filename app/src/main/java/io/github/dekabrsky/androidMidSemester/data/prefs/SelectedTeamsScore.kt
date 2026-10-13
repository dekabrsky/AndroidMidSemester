package io.github.dekabrsky.androidMidSemester.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("selected_teams")

private val KEY_TEAM_IDS = stringSetPreferencesKey("selected_team_ids")

class SelectedTeamsStore(private val context: Context) {

    val selectedIds: Flow<Set<Long>> = context.dataStore.data
        .map { prefs ->
            prefs[KEY_TEAM_IDS]
                .orEmpty()
                .mapNotNull { it.toLongOrNull() }
                .toSet()
        }

    suspend fun toggle(id: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_TEAM_IDS].orEmpty()
            val key = id.toString()
            val next = if (key in current) current - key else current + key
            prefs[KEY_TEAM_IDS] = next
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(KEY_TEAM_IDS) }
    }
}