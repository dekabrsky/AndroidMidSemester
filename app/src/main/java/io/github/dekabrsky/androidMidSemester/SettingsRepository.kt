package io.github.dekabrsky.androidMidSemester

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Расширение для создания DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val FAVORITE_TEAMS_KEY = stringPreferencesKey("favorite_teams")

    // Возвращает Flow с Set ID избранных команд
    val favoriteTeamIds: Flow<Set<Long>> = context.dataStore.data
        .map { preferences ->
            val idsString = preferences[FAVORITE_TEAMS_KEY] ?: ""
            if (idsString.isBlank()) emptySet()
            else idsString.split(",").map { it.toLong() }.toSet()
        }

    // Сохраняет Set ID избранных команд
    suspend fun setFavoriteTeamIds(ids: Set<Long>) {
        context.dataStore.edit { preferences ->
            preferences[FAVORITE_TEAMS_KEY] = ids.joinToString(",")
        }
    }
}