package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.GameDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.ListEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.SingleEnvelope
import io.github.dekabrsky.androidMidSemester.data.api.dto.TeamListItemDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val TEAMS_LIMIT = 500

interface SStatsApi {
    @GET("Games/list?offset=0&limit=10")
    suspend fun gamesOn(
        @Query("Date") date: String,
        @Query("Team") teamIds: String? = null,
    ): ListEnvelope<GameDto>

    @GET("Games/{id}")
    suspend fun game(@Path("id") id: Long): SingleEnvelope<GameDto>

    @GET("Teams/list")
    suspend fun teams(
        @Query("Country") country: String,
        @Query("Limit") limit: Int = TEAMS_LIMIT,
    ): ListEnvelope<TeamListItemDto>
}