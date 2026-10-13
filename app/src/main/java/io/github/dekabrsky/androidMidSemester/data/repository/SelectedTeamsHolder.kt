package io.github.dekabrsky.androidMidSemester.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SelectedTeamsHolder {

    private val _selectedNames = MutableStateFlow<Set<String>>(emptySet())
    val selectedNames: StateFlow<Set<String>> = _selectedNames.asStateFlow()

    fun toggle(teamName: String) {
        val normalized = teamName.trim().lowercase()
        _selectedNames.value = _selectedNames.value.let { current ->
            if (normalized in current) current - normalized else current + normalized
        }
    }

    fun isSelected(teamName: String): Boolean =
        teamName.trim().lowercase() in _selectedNames.value
}