package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesMemoryCache
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MatchRepositoryImplTest {

    @Test
    fun `revisited date is loaded from cache`() = runBlocking {
        val api = CountingApi()
        val repository = MatchRepositoryImpl(api, MatchesMemoryCache())
        val firstDate = LocalDate.of(2026, 10, 13)
        val secondDate = firstDate.plusDays(1)

        repository.matchesOn(firstDate)
        repository.matchesOn(secondDate)
        repository.matchesOn(firstDate)

        assertEquals(1, api.requestsByDate[firstDate.toString()])
        assertEquals(1, api.requestsByDate[secondDate.toString()])
    }
}

private class CountingApi : SStatsApi {
    val requestsByDate = mutableMapOf<String, Int>()

    override suspend fun gamesOn(date: String): ListEnvelope<GameDto> {
        requestsByDate[date] = requestsByDate.getOrDefault(date, 0) + 1
        return ListEnvelope(status = "success")
    }

    override suspend fun teams(country: String): ListEnvelope<TeamDto> =
        ListEnvelope(status = "success")

    override suspend fun game(id: Long): SingleEnvelope<GameDto> =
        SingleEnvelope(status = "success")
}
