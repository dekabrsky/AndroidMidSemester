package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SStatsTeamDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response

interface SStatsApi {

    /** @param date дата в формате "yyyy-MM-dd"; API отдаёт матчи страницами. */
    @GET("Games/list")
    suspend fun gamesOn(
        @Query("Date") date: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): ListEnvelope<GameDto>

    @GET("Games/{id}")
    suspend fun game(@Path("id") id: Long): SingleEnvelope<GameDto>

    @GET("Teams/list")
    suspend fun teams(
        @Query("Country") country: String = "Russia",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100,
    ): Response<TeamEnvelope<SStatsTeamDto>>
}
