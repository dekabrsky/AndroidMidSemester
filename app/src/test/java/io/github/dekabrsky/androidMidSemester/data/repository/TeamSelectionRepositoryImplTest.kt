package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.domain.usecase.ObserveSelectedTeamIdsUseCase
import io.github.dekabrsky.androidMidSemester.domain.usecase.SaveSelectedTeamIdsUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TeamSelectionRepositoryImplTest {
    @Test
    fun `saved selection is shared by readers and can be reset`() {
        val repository = TeamSelectionRepositoryImpl()
        val observe = ObserveSelectedTeamIdsUseCase(repository)
        val save = SaveSelectedTeamIdsUseCase(repository)
        val selection = observe()

        assertTrue(selection.value.isEmpty())
        save(setOf(555L, 596L))
        assertEquals(setOf(555L, 596L), selection.value)
        save(emptySet())
        assertTrue(selection.value.isEmpty())
    }

    @Test
    fun `caller mutations cannot change a saved selection`() {
        val repository = TeamSelectionRepositoryImpl()
        val ids = mutableSetOf(555L)

        repository.setSelectedTeamIds(ids)
        ids += 596L

        assertEquals(setOf(555L), repository.selectedTeamIds.value)
        assertTrue(TeamSelectionRepositoryImpl().selectedTeamIds.value.isEmpty())
    }
}
