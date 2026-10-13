package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SStatsTeamRepositoryTest {

    @Test
    fun `loads teams filtered by Russia`() = runBlocking {
        val api = TeamsApi()
        val repository = SStatsTeamRepository(api)

        val teams = repository.teams("Russia")

        assertEquals("Russia", api.requestedCountry)
        assertEquals(555L, teams.single().id)
        assertEquals("CSKA Moscow", teams.single().name)
        assertEquals("https://sstats.net/assets/logos/555.png", teams.single().logoUrl)
    }
}

private class TeamsApi : SStatsApi {
    var requestedCountry: String? = null

    override suspend fun teams(country: String): ListEnvelope<TeamDto> {
        requestedCountry = country
        return ListEnvelope(
            status = "OK",
            data = listOf(TeamDto(id = 555, name = "CSKA Moscow")),
        )
    }

    override suspend fun gamesOn(date: String): ListEnvelope<GameDto> =
        ListEnvelope(status = "OK")

    override suspend fun game(id: Long): SingleEnvelope<GameDto> =
        SingleEnvelope(status = "OK")
}
