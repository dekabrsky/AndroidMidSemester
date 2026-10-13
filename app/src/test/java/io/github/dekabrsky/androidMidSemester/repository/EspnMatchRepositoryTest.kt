package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.EspnFixtures
import io.github.dekabrsky.androidMidSemester.data.api.EspnApi
import io.github.dekabrsky.androidMidSemester.data.cache.MatchesCache
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.time.LocalDate

class EspnMatchRepositoryTest {
    private val requests = mutableListOf<Request>()
    private val date = LocalDate.of(2026, 10, 11)

    @Test
    fun `scoreboard uses the ESPN URL league and compact date and stays cached`() = runBlocking {
        val repository = repository { EspnFixtures.text("scoreboard") }

        val matches = repository.matchesOn(date)

        assertEquals(3, matches.size)
        assertEquals(401879843L, matches.first().id)
        assertSame(matches, repository.matchesOn(date))
        assertEquals(1, requests.size)
        assertEquals(
            "https://site.api.espn.com/apis/site/v2/sports/soccer/rus.1/scoreboard?dates=20261011",
            requests.single().url.toString(),
        )
        assertEquals("GET", requests.single().method)
    }

    @Test
    fun `returning from an empty day preserves separate cached dates`() = runBlocking {
        val repository = repository { request ->
            if (request.url.queryParameter("dates") == "20261011") {
                EspnFixtures.text("scoreboard")
            } else {
                """{"events": []}"""
            }
        }
        val matches = repository.matchesOn(date)
        val empty = repository.matchesOn(date.plusDays(1))

        assertTrue(empty.isEmpty())
        assertSame(matches, repository.matchesOn(date))
        assertSame(empty, repository.matchesOn(date.plusDays(1)))
        assertEquals(listOf("20261011", "20261012"), requests.map { it.url.queryParameter("dates") })
    }

    @Test
    fun `summary implements the existing match by ID contract`() = runBlocking {
        val repository = repository { EspnFixtures.text("summary") }

        val match = repository.match(401879843L)

        assertEquals(401879843L, match.id)
        assertEquals("Akhmat Grozny", match.home.name)
        assertEquals("https://a.espncdn.com/i/teamlogos/soccer/500/2991.png", match.home.logoUrl)
        assertEquals(
            "https://site.api.espn.com/apis/site/v2/sports/soccer/rus.1/summary?event=401879843",
            requests.single().url.toString(),
        )
    }

    @Test
    fun `a missing summary reports a missing match`() = runBlocking {
        val repository = repository { "{}" }

        try {
            repository.match(401879843L)
            fail("Expected a missing match")
        } catch (_: NoSuchElementException) {
            assertEquals(1, requests.size)
        }
    }

    @Test
    fun `a failed ESPN request can be retried and then cached`() = runBlocking {
        var failRequest = true
        val repository = repository {
            if (failRequest) throw IOException("Network unavailable")
            EspnFixtures.text("scoreboard")
        }
        try {
            repository.matchesOn(date)
            fail("Expected a network error")
        } catch (_: IOException) {
            failRequest = false
        }

        val matches = repository.matchesOn(date)

        assertSame(matches, repository.matchesOn(date))
        assertEquals(2, requests.size)
    }

    private fun repository(body: (Request) -> String): EspnMatchRepository {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()
            requests += request
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(body(request).toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        val api = Retrofit.Builder()
            .baseUrl("https://site.api.espn.com/")
            .client(client)
            .addConverterFactory(EspnFixtures.json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(EspnApi::class.java)
        return EspnMatchRepository(api, MatchesCache())
    }
}