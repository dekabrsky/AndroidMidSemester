package io.github.dekabrsky.androidMidSemester

import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.presentation.matches.filterMatchesByTeams
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class MatchFilteringTest {

    private fun team(id: Long, name: String) = Team(id = id, name = name, logoUrl = null)

    private fun match(id: Long, homeId: Long, awayId: Long) = Match(
        id = id,
        home = team(homeId, "Home $homeId"),
        away = team(awayId, "Away $awayId"),
        homeScore = null,
        awayScore = null,
        status = MatchStatus.SCHEDULED,
        kickoff = LocalDateTime.of(2026, 10, 11, 13, 0),
        league = "Test League",
        round = "Round 1",
    )

    private val matches = listOf(
        match(id = 1, homeId = 555, awayId = 558),
        match(id = 2, homeId = 596, awayId = 597),
        match(id = 3, homeId = 621, awayId = 779),
    )

    @Test
    fun `empty selection keeps all matches`() {
        assertEquals(matches, filterMatchesByTeams(matches, emptySet()))
    }

    @Test
    fun `filters by home team`() {
        assertEquals(listOf(matches[0]), filterMatchesByTeams(matches, setOf(555L)))
    }

    @Test
    fun `filters by away team`() {
        assertEquals(listOf(matches[1]), filterMatchesByTeams(matches, setOf(597L)))
    }

    @Test
    fun `keeps matches of any selected team`() {
        assertEquals(
            listOf(matches[0], matches[2]),
            filterMatchesByTeams(matches, setOf(555L, 621L)),
        )
    }

    @Test
    fun `unknown team id yields empty list`() {
        assertEquals(emptyList<Match>(), filterMatchesByTeams(matches, setOf(9999L)))
    }
}
