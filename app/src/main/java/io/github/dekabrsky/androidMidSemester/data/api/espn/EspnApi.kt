package io.github.dekabrsky.androidMidSemester.data.api.espn

import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.espn.dto.EspnSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface EspnApi {

    @GET("apis/site/v2/sports/soccer/{league}/scoreboard")
    suspend fun scoreboard(
        @Path("league") league: String,
        @Query("dates") dates: String,
    ): EspnScoreboardDto

    /** Детали одного матча. */
    @GET("apis/site/v2/sports/soccer/{league}/summary")
    suspend fun summary(
        @Path("league") league: String,
        @Query("event") eventId: Long,
    ): EspnSummaryDto
}
