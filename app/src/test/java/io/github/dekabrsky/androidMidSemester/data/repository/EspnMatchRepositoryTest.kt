package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.espn.EspnApi
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnCompetitionDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnCompetitorDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnEventDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnLeagueDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnStatusDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnStatusTypeDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnTeamDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnSummaryDto
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import io.github.dekabrsky.androidMidSemester.domain.model.MatchStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class EspnMatchRepositoryTest {

    @Test
    fun `loads and maps ESPN scoreboard`() = runBlocking {
        val api = FakeEspnApi()
        val repository = EspnMatchRepository(api, MatchesMemoryCache())

        val matches = repository.matchesOn(LocalDate.of(2026, 10, 11))

        assertEquals("20261011", api.requestedDates.single())
        assertEquals(1, matches.size)
        with(matches.single()) {
            assertEquals(401879843L, id)
            assertEquals("Akhmat Grozny", home.name)
            assertEquals("FC Baltika Kaliningrad", away.name)
            assertEquals(0, homeScore)
            assertEquals(0, awayScore)
            assertEquals(MatchStatus.FINISHED, status)
            assertEquals("Russian Premier League", league)
            assertEquals("Russian Premier", round)
        }
    }

    @Test
    fun `revisited ESPN date is loaded from cache`() = runBlocking {
        val api = FakeEspnApi()
        val repository = EspnMatchRepository(api, MatchesMemoryCache())
        val date = LocalDate.of(2026, 10, 11)

        repository.matchesOn(date)
        repository.matchesOn(date)

        assertEquals(1, api.requestedDates.size)
    }
}

private class FakeEspnApi : EspnApi {
    val requestedDates = mutableListOf<String>()

    override suspend fun scoreboard(date: String): EspnScoreboardDto {
        requestedDates += date
        return EspnScoreboardDto(
            leagues = listOf(EspnLeagueDto(name = "Russian Premier League")),
            events = listOf(
                EspnEventDto(
                    id = "401879843",
                    date = "2026-10-11T11:00Z",
                    competitions = listOf(
                        EspnCompetitionDto(
                            competitors = listOf(
                                competitor("2991", "home", "Akhmat Grozny", "0"),
                                competitor("21949", "away", "FC Baltika Kaliningrad", "0"),
                            ),
                            status = EspnStatusDto(
                                type = EspnStatusTypeDto(completed = true),
                            ),
                            altGameNote = "Russian Premier",
                        )
                    ),
                )
            ),
        )
    }

    override suspend fun summary(eventId: String): EspnSummaryDto =
        error("Summary is not expected in this test")

    private fun competitor(
        id: String,
        homeAway: String,
        name: String,
        score: String,
    ) = EspnCompetitorDto(
        id = id,
        homeAway = homeAway,
        score = score,
        team = EspnTeamDto(
            id = id,
            displayName = name,
            logo = "https://a.espncdn.com/i/teamlogos/soccer/500/$id.png",
        ),
    )
}
