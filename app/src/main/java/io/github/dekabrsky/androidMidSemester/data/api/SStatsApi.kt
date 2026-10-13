package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SStatsApi {

    @GET("Games/list?limit=1000")
    suspend fun gamesOn(
        @Query("Date") date: String,
        @Query("Offset") offset: Int,
        @Query("TimeZone") timeZone: Int,
    ): ListEnvelope<GameDto>

    @GET("Teams/list?limit=1000")
    suspend fun teams(
        @Query("Country") country: String,
        @Query("Offset") offset: Int,
    ): ListEnvelope<TeamDto>

    @GET("Games/{id}")
    suspend fun game(@Path("id") id: Long): SingleEnvelope<GameDto>
}
