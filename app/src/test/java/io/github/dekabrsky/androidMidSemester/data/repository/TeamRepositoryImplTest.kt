package io.github.dekabrsky.androidMidSemester.data.repository

import io.github.dekabrsky.androidMidSemester.data.api.SStatsApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
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

class TeamRepositoryImplTest {
    private val requests = mutableListOf<Request>()

    @Test
    fun `country is sent to the API and logo from the response is preserved`() = runBlocking {
        val repository = TeamRepositoryImpl(api { teamsJson() })

        val teams = repository.teamsFrom("Russia")

        assertEquals("https://api.sstats.net/Teams/list?Country=Russia&Offset=0&Limit=1000", requests.single().url.toString())
        assertEquals(555L, teams.single().id)
        assertEquals("CSKA Moscow", teams.single().name)
        assertEquals("https://sstats.net/assets/logos/555.png", teams.single().logoUrl)
        assertSame(teams, repository.teamsFrom("Russia"))
        assertEquals(1, requests.size)
    }

    @Test
    fun `total count loads remaining pages even when the first page is short`() = runBlocking {
        val repository = TeamRepositoryImpl(api { request ->
            if (request.url.queryParameter("Offset") == "0") {
                """{"status":"OK","TotalCount":3,"data":[{"id":3,"name":"Zenit"},{"id":1,"name":"Akhmat"}]}"""
            } else {
                """{"status":"OK","TotalCount":3,"data":[{"id":2,"name":"CSKA Moscow"}]}"""
            }
        })

        val teams = repository.teamsFrom("Russia")

        assertEquals(listOf("Akhmat", "CSKA Moscow", "Zenit"), teams.map { it.name })
        assertEquals(listOf("0", "2"), requests.map { it.url.queryParameter("Offset") })
        assertSame(teams, repository.teamsFrom("Russia"))
        assertEquals(2, requests.size)
    }

    @Test
    fun `empty lists are cached separately by country`() = runBlocking {
        val repository = TeamRepositoryImpl(api { """{"status":"OK","TotalCount":0,"data":[]}""" })

        assertTrue(repository.teamsFrom("Russia").isEmpty())
        assertTrue(repository.teamsFrom("Russia").isEmpty())
        assertTrue(repository.teamsFrom("France").isEmpty())

        assertEquals(listOf("Russia", "France"), requests.map { it.url.queryParameter("Country") })
    }

    @Test
    fun `a network failure is retried rather than cached`() = runBlocking {
        var failure = true
        val repository = TeamRepositoryImpl(api {
            if (failure) throw IOException("Offline")
            teamsJson()
        })
        try {
            repository.teamsFrom("Russia")
            fail("Expected a network failure")
        } catch (_: IOException) {
            failure = false
        }

        assertEquals(1, repository.teamsFrom("Russia").size)
        repository.teamsFrom("Russia")
        assertEquals(2, requests.size)
    }

    @Test
    fun `an error envelope is not cached as an empty country`() = runBlocking {
        var failure = true
        val repository = TeamRepositoryImpl(api {
            if (failure) """{"status":"ERROR","data":[]}""" else teamsJson()
        })
        try {
            repository.teamsFrom("Russia")
            fail("Expected an API error")
        } catch (_: IllegalStateException) {
            failure = false
        }

        assertEquals(1, repository.teamsFrom("Russia").size)
        assertEquals(2, requests.size)
    }

    @Test
    fun `team ID commas stay unescaped to respect the server URL length limit`() = runBlocking {
        val api = api { """{"status":"OK","data":[]}""" }

        api.gamesOn(date = "2026-10-11", teamIds = "555,596")

        assertEquals("555,596", requests.single().url.queryParameter("Team"))
        assertTrue(requests.single().url.toString().contains("Team=555,596"))
    }

    private fun teamsJson() = """{
        "status":"OK", "TotalCount":1,
        "data":[{"id":555,"name":"CSKA Moscow","logoUrl":"https://sstats.net/assets/logos/555.png",
                 "country":{"code":"RU","name":"Russia"}}]
    }"""

    private fun api(body: (Request) -> String): SStatsApi {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()
            requests += request
            Response.Builder().request(request).protocol(Protocol.HTTP_1_1)
                .code(200).message("OK")
                .body(body(request).toResponseBody("application/json".toMediaType())).build()
        }.build()
        return Retrofit.Builder().baseUrl("https://api.sstats.net/").client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build().create(SStatsApi::class.java)
    }

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }
}
