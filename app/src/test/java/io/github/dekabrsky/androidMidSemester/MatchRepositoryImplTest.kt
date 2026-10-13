package io.github.dekabrsky.androidMidSemester

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.LeagueDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SeasonDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SStatsTeamDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import io.github.dekabrsky.androidMidSemester.data.repository.MatchRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate

class MatchRepositoryImplTest {
    @Test
    fun matchesOnLoadsPagesPastFirstHundredAndKeepsRussianTeamIds() = runBlocking {
        val api = PagedGamesApi()
        val matches = MatchRepositoryImpl(api).matchesOn(LocalDate.of(2026, 10, 13))

        assertEquals(listOf(0, 100), api.offsets)
        assertEquals(listOf("2026-10-13", "2026-10-13"), api.dates)
        assertEquals(101, matches.size)
        assertEquals(6790L, matches.last().away.id)
    }

    private class PagedGamesApi : SStatsApi {
        val offsets = mutableListOf<Int>()
        val dates = mutableListOf<String>()

        override suspend fun gamesOn(date: String, offset: Int, limit: Int): ListEnvelope<GameDto> {
            dates += date
            offsets += offset
            val data = if (offset == 0) {
                List(100) { index -> game(index.toLong(), 1L, 2L) }
            } else {
                listOf(game(100L, 19449L, 6790L))
            }
            return ListEnvelope(status = "OK", data = data)
        }

        override suspend fun game(id: Long): SingleEnvelope<GameDto> = SingleEnvelope(status = "OK")
        override suspend fun teams(country: String, offset: Int, limit: Int): Response<TeamEnvelope<SStatsTeamDto>> =
            Response.success(TeamEnvelope(status = "OK"))

        private fun game(id: Long, homeId: Long, awayId: Long) = GameDto(
            id = id,
            dateUtc = 1791896400,
            statusName = "Scheduled",
            homeTeam = TeamDto(homeId, "Home $homeId"),
            awayTeam = TeamDto(awayId, "Away $awayId"),
            season = SeasonDto(LeagueDto("Cup")),
            roundName = "Round 1",
        )
    }
}
