package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ESPNApi {
    @GET("apis/site/v2/sports/soccer/{league}/scoreboard")
    suspend fun getScoreboard(
        @Path("league") league: String = DEFAULT_LEAGUE,
        @Query("dates") dates: String,
    ): EspnScoreboardDto

    @GET("apis/site/v2/sports/soccer/{league}/summary")
    suspend fun game(@Path("league") league: String,
                     @Query("event") id: Long,): EspnSummaryDto

    companion object {
        const val BASE_URL = "https://site.api.espn.com/"
        const val DEFAULT_LEAGUE = "all"
    }
}