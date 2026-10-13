package io.github.dekabrsky.androidMidSemester.data.api.espn

import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnScoreboardDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EspnApi {
    @GET("apis/site/v2/sports/soccer/rus.1/scoreboard")
    suspend fun scoreboard(@Query("dates") dates: String): EspnScoreboardDto
}