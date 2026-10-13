package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardResponse
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnSummaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EspnApi {
    @GET("apis/site/v2/sports/soccer/rus.1/scoreboard")
    suspend fun getScoreboard(@Query("dates") date: String): EspnScoreboardResponse

    @GET("apis/site/v2/sports/soccer/rus.1/summary")
    suspend fun getSummary(@Query("event") id: String): EspnSummaryResponse
}