package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import retrofit2.http.GET
import retrofit2.http.Query

interface EspnApi {

    @GET("apis/site/v2/sports/soccer/rus.1/scoreboard")
    suspend fun gamesOn(
        @Query("dates") date: String
    ): EspnScoreboardDto

    @GET("apis/site/v2/sports/soccer/rus.1/summary")
    suspend fun game(
        @Query("event") id: Long
    ): EspnScoreboardDto
}