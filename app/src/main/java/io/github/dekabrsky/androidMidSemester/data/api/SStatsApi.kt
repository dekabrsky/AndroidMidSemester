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
        // Значение состоит только из числовых ID и запятых. Кодирование запятых
        // увеличивает URL со всеми командами России сверх лимита сервера.
        @Query(value = "Team", encoded = true) teamIds: String,
        @Query("Offset") offset: Int = 0,
        @Query("Limit") limit: Int = 1000,
    ): ListEnvelope<GameDto>

    @GET("Teams/list")
    suspend fun teams(
        @Query("Country") country: String,
        @Query("Offset") offset: Int = 0,
        @Query("Limit") limit: Int = 1000,
    ): ListEnvelope<TeamDto>

    @GET("Games/{id}")
    suspend fun game(@Path("id") id: Long): SingleEnvelope<GameDto>
}