package io.github.dekabrsky.androidMidSemester.data.selection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Выбранные пользователем команды — простой in-memory store (без постоянного хранения).
 * В Koin зарегистрирован как single, поэтому выбор общий для экранов
 * и живёт столько же, сколько и приложение.
 *
 * Пустое множество означает «фильтр не задан» — показываем все матчи.
 */
class TeamSelectionStore {

    private val _selectedTeamIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTeamIds: StateFlow<Set<Long>> = _selectedTeamIds.asStateFlow()

    /** Включает/выключает команду в фильтре. */
    fun toggle(teamId: Long) {
        _selectedTeamIds.value = _selectedTeamIds.value.toMutableSet().apply {
            if (!add(teamId)) remove(teamId)
        }
    }

    /** Сбрасывает фильтр — снова показываем все матчи. */
    fun clear() {
        _selectedTeamIds.value = emptySet()
    }
}
