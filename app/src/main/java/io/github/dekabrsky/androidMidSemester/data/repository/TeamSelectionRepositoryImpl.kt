package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Выбор команд хранится в памяти процесса независимо от экранов. */
class TeamSelectionRepositoryImpl : TeamSelectionRepository {
    private val ids = MutableStateFlow<Set<Long>>(emptySet())
    override val selectedTeamIds = ids.asStateFlow()

    override fun setSelectedTeamIds(ids: Set<Long>) {
        this.ids.value = ids.toSet()
    }
}
