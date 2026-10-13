package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.espn.EspnScoreboardDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EspnApi {

    /**
     * Запрос табло матчей от ESPN API.
     * @param league код лиги (по умолчанию "all" — все мировые лиги, либо "rus.1", "eng.1" и др.)
     * @param date дата в формате "yyyyMMdd"
     */
    @GET("sports/soccer/{league}/scoreboard")
    suspend fun scoreboard(
        @Path("league") league: String = "all",
        @Query("dates") date: String,
    ): EspnScoreboardDto
}
