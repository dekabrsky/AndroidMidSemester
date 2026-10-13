package io.github.dekabrsky.androidMidSemester.domain.usecase

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.MatchRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GetMatchesUseCaseTest {
    private val date = LocalDate.of(2026, 10, 11)
    private val matches = listOf(match(1, 555, 596), match(2, 558, 2006), match(3, 777, 555))
    private val getMatches = GetMatchesUseCase(object : MatchRepository {
        override suspend fun matchesOn(date: LocalDate) = matches
        override suspend fun match(id: Long) = matches.single { it.id == id }
    })

    @Test
    fun `no selection shows every available Russian match`() = runBlocking {
        assertSame(matches, getMatches(date))
    }

    @Test
    fun `a selected team matches either home or away`() = runBlocking {
        assertEquals(listOf(1L, 3L), getMatches(date, setOf(555L)).map { it.id })
    }

    @Test
    fun `matches of any selected team are included without duplicates`() = runBlocking {
        assertEquals(listOf(1L, 3L), getMatches(date, setOf(555L, 596L)).map { it.id })
        assertEquals(listOf(1L, 2L, 3L), getMatches(date, setOf(555L, 558L)).map { it.id })
    }

    @Test
    fun `clearing selection restores results without mutating the original list`() = runBlocking {
        getMatches(date, setOf(558L))

        assertSame(matches, getMatches(date, emptySet()))
        assertEquals(3, matches.size)
    }

    @Test
    fun `a selected team without games produces an empty list`() = runBlocking {
        assertTrue(getMatches(date, setOf(999L)).isEmpty())
    }

    private fun match(id: Long, homeId: Long, awayId: Long) = Match(
        id = id, home = Team(homeId, "Home", null), away = Team(awayId, "Away", null),
        homeScore = 0, awayScore = 0, status = MatchStatus.FINISHED,
        kickoff = date.atTime(16, 0), league = "Premier League", round = "11",
    )
}
