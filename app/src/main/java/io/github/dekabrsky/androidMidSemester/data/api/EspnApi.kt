package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EspnApi {

    @GET("apis/site/v2/sports/soccer/{league}/scoreboard")
    suspend fun scoreboard(
        @Path("league") league: String,
        @Query("dates") dates: String,
    ): EspnScoreboardDto
}