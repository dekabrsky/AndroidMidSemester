package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SStatsApi {

    @GET("Games/list")
    suspend fun gamesOn(
        @Query("Date") date: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100,
        @Query("Team") teamIds: String? = null,
    ): ListEnvelope<GameDto>

    @GET("Teams/list")
    suspend fun teamsIn(
        @Query("Country") country: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100,
    ): ListEnvelope<TeamDto>

    @GET("Games/{id}")
    suspend fun game(@Path("id") id: Long): SingleEnvelope<GameDto>
}
