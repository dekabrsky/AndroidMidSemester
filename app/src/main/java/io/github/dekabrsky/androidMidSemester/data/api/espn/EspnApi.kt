package io.github.dekabrsky.androidMidSemester.data.api.espn

import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnSummaryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface EspnApi {

    @GET("apis/site/v2/sports/soccer/rus.1/scoreboard")
    suspend fun scoreboard(@Query("dates") date: String): EspnScoreboardDto

    @GET("apis/site/v2/sports/soccer/rus.1/summary")
    suspend fun summary(@Query("event") eventId: String): EspnSummaryDto
}
