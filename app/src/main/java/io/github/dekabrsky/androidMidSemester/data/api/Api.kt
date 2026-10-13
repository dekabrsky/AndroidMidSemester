package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.Scoreboard
import io.github.dekabrsky.androidMidSemester.data.api.dto.Summary
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("apis/site/v2/sports/soccer/rus.1/scoreboard")
    suspend fun scoreboard(@Query("dates") date: String): Scoreboard

    @GET("apis/site/v2/sports/soccer/rus.1/summary")
    suspend fun summary(@Query("event") id: Long): Summary
}