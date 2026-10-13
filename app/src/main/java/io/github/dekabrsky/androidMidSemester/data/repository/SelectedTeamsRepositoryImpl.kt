package io.github.dekabrsky.androidMidSemester.data.repository

import android.content.Context
import io.github.dekabrsky.androidMidSemester.domain.repository.SelectedTeamsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREFS_NAME = "team_filter"
private const val KEY_SELECTED_IDS = "selected_team_ids"

/**
 * Выбранные пользователем команды. Хранятся в SharedPreferences, чтобы выбор
 * переживал перезапуск приложения, и отдаются наружу как Flow, чтобы список матчей
 * сам перезагружался при изменении выбора.
 */
class SelectedTeamsRepositoryImpl(
    context: Context,
) : SelectedTeamsRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val selected = MutableStateFlow(readFromPrefs())

    override val selectedTeamIds: Flow<Set<Long>> = selected.asStateFlow()

    override fun toggle(teamId: Long) {
        val current = selected.value
        save(if (teamId in current) current - teamId else current + teamId)
    }

    override fun clear() {
        save(emptySet())
    }

    private fun readFromPrefs(): Set<Long> =
        prefs.getStringSet(KEY_SELECTED_IDS, emptySet())
            .orEmpty()
            .mapNotNull { it.toLongOrNull() }
            .toSet()

    private fun save(ids: Set<Long>) {
        selected.value = ids
        prefs.edit()
            .putStringSet(KEY_SELECTED_IDS, ids.map { it.toString() }.toSet())
            .apply()
    }
}
