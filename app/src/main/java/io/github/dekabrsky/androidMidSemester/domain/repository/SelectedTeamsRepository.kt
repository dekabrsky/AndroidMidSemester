package io.github.dekabrsky.androidMidSemester.domain.repository

import kotlinx.coroutines.flow.Flow

/** Команды, матчи которых пользователь хочет видеть в списке. */
interface SelectedTeamsRepository {
    /** Текущий выбор; пустое множество означает "фильтра нет, показываем все матчи". */
    val selectedTeamIds: Flow<Set<Long>>

    /** Добавить команду в выбор или убрать её оттуда. */
    fun toggle(teamId: Long)

    /** Сбросить выбор целиком. */
    fun clear()
}
