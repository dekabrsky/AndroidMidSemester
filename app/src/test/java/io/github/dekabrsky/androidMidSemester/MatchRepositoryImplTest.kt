package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.LeagueDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SeasonDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

class MatchRepositoryImplTest {
    private val date = LocalDate.of(2026, 10, 13)
    private val api = FakeApi()
    private val cache = MatchesCache()
    private val repository = MatchRepositoryImpl(api, cache)

    @Test
    fun `returning to a visited date uses its cached matches`() = runBlocking {
        api.responses[date.toString()] = listOf(game(1))
        api.responses[date.plusDays(1).toString()] = listOf(game(2))

        val today = repository.matchesOn(date)
        val tomorrow = repository.matchesOn(date.plusDays(1))

        assertSame(today, repository.matchesOn(date))
        assertSame(tomorrow, repository.matchesOn(date.plusDays(1)))
        assertEquals(1L, today.single().id)
        assertEquals(2L, tomorrow.single().id)
        assertEquals(listOf(date.toString(), date.plusDays(1).toString()), api.requestedDates)
    }

    @Test
    fun `an empty day is also cached`() = runBlocking {
        assertTrue(repository.matchesOn(date).isEmpty())
        assertTrue(repository.matchesOn(date).isEmpty())

        assertEquals(listOf(date.toString()), api.requestedDates)
    }

    @Test
    fun `a failed request can be retried and its successful result is cached`() = runBlocking {
        api.failure = IOException("Network unavailable")
        try {
            repository.matchesOn(date)
            fail("Expected a network error")
        } catch (_: IOException) {
            // Ошибка не должна сохраняться в кэше.
        }
        api.failure = null
        api.responses[date.toString()] = listOf(game(1))

        val result = repository.matchesOn(date)

        assertSame(result, repository.matchesOn(date))
        assertEquals(listOf(date.toString(), date.toString()), api.requestedDates)
    }

    @Test
    fun `concurrent requests for the same date share one network result`() = runBlocking {
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        api.beforeResponse = {
            started.complete(Unit)
            release.await()
        }
        api.responses[date.toString()] = listOf(game(1))

        val first = async { repository.matchesOn(date) }
        started.await()
        val second = async(start = CoroutineStart.UNDISPATCHED) { repository.matchesOn(date) }
        release.complete(Unit)

        assertSame(first.await(), second.await())
        assertEquals(listOf(date.toString()), api.requestedDates)
    }

    @Test
    fun `another repository instance reuses the application cache`() = runBlocking {
        val result = repository.matchesOn(date)
        val anotherRepository = MatchRepositoryImpl(api, cache)

        assertSame(result, anotherRepository.matchesOn(date))
        assertEquals(listOf(date.toString()), api.requestedDates)
    }

    @Test
    fun `a new application cache loads the date again`() = runBlocking {
        repository.matchesOn(date)
        val anotherRepository = MatchRepositoryImpl(api, MatchesCache())
        anotherRepository.matchesOn(date)

        assertEquals(listOf(date.toString(), date.toString()), api.requestedDates)
    }

    private fun game(id: Long) = GameDto(
        id = id,
        dateUtc = 1791849600L,
        statusName = "Finished",
        homeResult = 2,
        awayResult = 0,
        homeTeam = TeamDto(id = 1, name = "Home"),
        awayTeam = TeamDto(id = 2, name = "Away"),
        season = SeasonDto(league = LeagueDto(name = "League")),
        roundName = "Round 1",
    )

    private class FakeApi : SStatsApi {
        val requestedDates = mutableListOf<String>()
        val responses = mutableMapOf<String, List<GameDto>>()
        var failure: IOException? = null
        var beforeResponse: suspend () -> Unit = {}

        override suspend fun gamesOn(date: String): ListEnvelope<GameDto> {
            requestedDates += date
            beforeResponse()
            failure?.let { throw it }
            return ListEnvelope(status = "OK", data = responses[date].orEmpty())
        }

        override suspend fun game(id: Long): SingleEnvelope<GameDto> =
            error("Match details are not requested by these tests")
    }
}