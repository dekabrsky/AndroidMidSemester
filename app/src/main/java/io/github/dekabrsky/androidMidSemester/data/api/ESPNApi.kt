package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ESPNApi {
    @GET("apis/site/v2/sports/soccer/rus.1/scoreboard")
    suspend fun getScoreboard(
        @Query("dates") dates: String,
    ): EspnScoreboardDto
}