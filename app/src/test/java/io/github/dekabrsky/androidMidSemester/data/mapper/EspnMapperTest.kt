package io.github.dekabrsky.androidMidSemester.data.mapper

import io.github.dekabrsky.androidMidSemester.data.EspnFixtures
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnStatusDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnStatusTypeDto
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class EspnMapperTest {
    private val scoreboard = EspnFixtures.scoreboard()
    private val event = scoreboard.events.first()
    private val league = scoreboard.leagues.first().name
    private val zone = ZoneId.of("Asia/Yekaterinburg")

    @Test
    fun `real scoreboard maps IDs teams score league and local kickoff`() {
        val match = event.toMatch(league, zone)

        assertEquals(401879843L, match.id)
        assertEquals(2991L, match.home.id)
        assertEquals("Akhmat Grozny", match.home.name)
        assertEquals("FC Baltika Kaliningrad", match.away.name)
        assertEquals("https://a.espncdn.com/i/teamlogos/soccer/500/2991.png", match.home.logoUrl)
        assertEquals(0, match.homeScore)
        assertEquals(0, match.awayScore)
        assertEquals(MatchStatus.FINISHED, match.status)
        assertEquals(LocalDateTime.of(2026, 10, 11, 16, 0), match.kickoff)
        assertEquals("Russian Premier League", match.league)
        assertEquals("", match.round)
    }

    @Test
    fun `home and away are determined by the flag rather than array order`() {
        val competition = event.competitions.first()
        val reversed = event.copy(
            competitions = listOf(competition.copy(competitors = competition.competitors.reversed()))
        )

        assertEquals(event.toMatch(league, zone), reversed.toMatch(league, zone))
    }

    @Test
    fun `real upcoming fixture hides the API zero scores`() {
        val upcoming = EspnFixtures.scoreboard("upcoming").events.first().toMatch(league, zone)

        assertEquals(MatchStatus.SCHEDULED, upcoming.status)
        assertNull(upcoming.homeScore)
        assertNull(upcoming.awayScore)
    }

    @Test
    fun `a live match keeps the current score`() {
        val competition = event.competitions.first()
        val live = event.copy(competitions = listOf(competition.copy(
            status = EspnStatusDto(EspnStatusTypeDto(state = "in")),
            competitors = competition.competitors.map {
                it.copy(score = if (it.homeAway == "home") "2" else "1")
            },
        )))
        val match = live.toMatch(league, zone)

        assertEquals(MatchStatus.SCHEDULED, match.status)
        assertEquals(2, match.homeScore)
        assertEquals(1, match.awayScore)
    }

    @Test
    fun `incomplete scores do not render a null opponent score`() {
        val competition = event.competitions.first()
        val incomplete = event.copy(competitions = listOf(competition.copy(
            competitors = competition.competitors.map {
                if (it.homeAway == "away") it.copy(score = null) else it
            },
        )))
        val match = incomplete.toMatch(league, zone)

        assertNull(match.homeScore)
        assertNull(match.awayScore)
    }

    @Test
    fun `real summary uses the logos array and matches scoreboard data`() {
        val header = checkNotNull(EspnFixtures.summary().header)

        assertEquals(event.toMatch(league, zone), header.toMatch(zone))
    }

    @Test
    fun `a missing logo remains absent for the UI placeholder`() {
        val competition = event.competitions.first()
        val noLogos = event.copy(competitions = listOf(competition.copy(
            competitors = competition.competitors.map {
                it.copy(team = it.team.copy(logo = null, logos = emptyList()))
            },
        )))

        assertNull(noLogos.toMatch(league, zone).home.logoUrl)
        assertNull(noLogos.toMatch(league, zone).away.logoUrl)
    }

    @Test
    fun `a malformed event fails instead of inventing a match`() {
        assertThrows(NoSuchElementException::class.java) {
            event.copy(competitions = emptyList()).toMatch(league, zone)
        }
    }
}
