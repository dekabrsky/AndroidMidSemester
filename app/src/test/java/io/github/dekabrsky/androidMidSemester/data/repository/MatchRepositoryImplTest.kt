package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.LeagueDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SeasonDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

class MatchRepositoryImplTest {
    private val date = LocalDate.of(2026, 10, 13)
    private val api = FakeApi()
    private val cache = MatchesCache()
    private val teams = object : TeamRepository {
        override suspend fun teamsFrom(country: String) = listOf(
            Team(1, "Home", null),
            Team(2, "Away", null)
        )
    }
    private val repository = MatchRepositoryImpl(api, cache, teams)

    @Test
    fun `returning to a visited date uses its cached matches`() = runBlocking {
        api.responses[date.toString()] = listOf(game(1))
        api.responses[date.plusDays(1).toString()] = listOf(game(2))

        val today = repository.matchesOn(date)
        val tomorrow = repository.matchesOn(date.plusDays(1))

        Assert.assertSame(today, repository.matchesOn(date))
        Assert.assertSame(tomorrow, repository.matchesOn(date.plusDays(1)))
        Assert.assertEquals(1L, today.single().id)
        Assert.assertEquals(2L, tomorrow.single().id)
        Assert.assertEquals(
            listOf(date.toString(), date.plusDays(1).toString()),
            api.requestedDates
        )
    }

    @Test
    fun `an empty day is also cached`() = runBlocking {
        Assert.assertTrue(repository.matchesOn(date).isEmpty())
        Assert.assertTrue(repository.matchesOn(date).isEmpty())

        Assert.assertEquals(listOf(date.toString()), api.requestedDates)
    }

    @Test
    fun `a failed request can be retried and its successful result is cached`() = runBlocking {
        api.failure = IOException("Network unavailable")
        try {
            repository.matchesOn(date)
            Assert.fail("Expected a network error")
        } catch (_: IOException) {
            // Ошибка не должна сохраняться в кэше.
        }
        api.failure = null
        api.responses[date.toString()] = listOf(game(1))

        val result = repository.matchesOn(date)

        Assert.assertSame(result, repository.matchesOn(date))
        Assert.assertEquals(listOf(date.toString(), date.toString()), api.requestedDates)
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

        Assert.assertSame(first.await(), second.await())
        Assert.assertEquals(listOf(date.toString()), api.requestedDates)
    }

    @Test
    fun `another repository instance reuses the application cache`() = runBlocking {
        val result = repository.matchesOn(date)
        val anotherRepository = MatchRepositoryImpl(api, cache, teams)

        Assert.assertSame(result, anotherRepository.matchesOn(date))
        Assert.assertEquals(listOf(date.toString()), api.requestedDates)
    }

    @Test
    fun `a new application cache loads the date again`() = runBlocking {
        repository.matchesOn(date)
        val anotherRepository = MatchRepositoryImpl(api, MatchesCache(), teams)
        anotherRepository.matchesOn(date)

        Assert.assertEquals(listOf(date.toString(), date.toString()), api.requestedDates)
    }

    @Test
    fun `server query includes every Russian team ID`() = runBlocking {
        repository.matchesOn(date)

        Assert.assertEquals(listOf("1,2"), api.requestedTeamIds)
        Assert.assertEquals(listOf(0), api.offsets)
    }

    @Test
    fun `all pages are loaded once before caching a day`() = runBlocking {
        api.responses[date.toString()] = (1L..1001L).map { game(it) }

        val result = repository.matchesOn(date)

        Assert.assertEquals(1001, result.size)
        Assert.assertSame(result, repository.matchesOn(date))
        Assert.assertEquals(listOf(0, 1000), api.offsets)
    }

    @Test
    fun `an empty country does not accidentally request worldwide matches`() = runBlocking {
        val noTeams = object : TeamRepository {
            override suspend fun teamsFrom(country: String) = emptyList<Team>()
        }
        val repository = MatchRepositoryImpl(api, cache, noTeams)

        Assert.assertTrue(repository.matchesOn(date).isEmpty())
        Assert.assertTrue(api.requestedDates.isEmpty())
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
        val requestedTeamIds = mutableListOf<String>()
        val offsets = mutableListOf<Int>()
        val responses = mutableMapOf<String, List<GameDto>>()
        var failure: IOException? = null
        var beforeResponse: suspend () -> Unit = {}

        override suspend fun gamesOn(date: String, teamIds: String, offset: Int, limit: Int): ListEnvelope<GameDto> {
            requestedDates += date
            requestedTeamIds += teamIds
            offsets += offset
            beforeResponse()
            failure?.let { throw it }
            return ListEnvelope(
                status = "OK",
                data = responses[date].orEmpty().drop(offset).take(limit)
            )
        }

        override suspend fun game(id: Long): SingleEnvelope<GameDto> =
            error("Match details are not requested by these tests")

        override suspend fun teams(country: String, offset: Int, limit: Int): ListEnvelope<TeamDto> =
            error("Teams are supplied by the fake repository")
    }
}