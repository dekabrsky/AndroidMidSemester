
package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnSummaryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface EspnApi {

    @GET("scoreboard")
    suspend fun gamesOn(
        @Query("dates") date: String
    ): EspnScoreboardDto

    @GET("summary")
    suspend fun game(
        @Query("event") id: Long
    ): EspnSummaryDto
}
