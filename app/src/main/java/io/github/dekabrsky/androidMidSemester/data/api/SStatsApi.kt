package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SStatsApi {
    @GET("Games/list?offset=0&limit=10")
    suspend fun gamesOn(@Query("Date") date: String): ListEnvelope<GameDto>

    @GET("Games/{id}")
    suspend fun game(@Path("id") id: Long): SingleEnvelope<GameDto>

    // <-- НОВОЕ: Получение списка команд по стране
    @GET("Teams/list")
    suspend fun getTeams(@Query("Country") country: String): ListEnvelope<TeamDto>
}
