package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Collections

class InMemoryTeamSelectionRepository : TeamSelectionRepository {
    private val _selectedTeams = MutableStateFlow<Map<Long, Team>>(emptyMap())
    override val selectedTeams = _selectedTeams.asStateFlow()

    override fun setSelected(team: Team, selected: Boolean) {
        _selectedTeams.update { current ->
            Collections.unmodifiableMap(if (selected) current + (team.id to team) else current - team.id)
        }
    }

    override fun clear() {
        _selectedTeams.value = emptyMap()
    }
}
