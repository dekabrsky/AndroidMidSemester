package io.github.dekabrsky.androidMidSemester.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test

class InMemoryTeamSelectionRepositoryTest {

    @Test
    fun `toggle adds and removes team`() {
        val repository = InMemoryTeamSelectionRepository()

        repository.toggle(555)
        assertEquals(setOf(555L), repository.selectedTeamIds.value)

        repository.toggle(555)
        assertEquals(emptySet<Long>(), repository.selectedTeamIds.value)
    }
}
