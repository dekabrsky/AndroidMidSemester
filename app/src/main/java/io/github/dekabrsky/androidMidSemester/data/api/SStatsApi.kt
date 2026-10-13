package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SStatsApi {

    /** @param date дата в формате "yyyy-MM-dd" */
    @GET("Games/list")
    suspend fun gamesOn(
        @Query("Date") date: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): ListEnvelope<GameDto>

    @GET("Games/{id}")
    suspend fun game(@Path("id") id: Long): SingleEnvelope<GameDto>

    @GET("Teams/list")
    suspend fun teamsFromCountry(@Query("Country") country: String): ListEnvelope<TeamDto>
}
