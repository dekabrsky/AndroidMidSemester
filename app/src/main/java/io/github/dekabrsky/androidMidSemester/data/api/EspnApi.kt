package io.github.dekabrsky.androidMidSemester.data.api

import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnScoreboardDto
import io.github.dekabrsky.androidMidSemester.data.api.dto.EspnSummaryDto
import retrofit2.http.GET
import retrofit2.http.Query

interface EspnApi {

    // Абсолютные URL позволяют использовать существующий Retrofit и его интерсепторы.
    @GET("https://site.api.espn.com/apis/site/v2/sports/soccer/rus.1/scoreboard")
    suspend fun scoreboard(@Query("dates") date: String): EspnScoreboardDto

    @GET("https://site.api.espn.com/apis/site/v2/sports/soccer/rus.1/summary")
    suspend fun summary(@Query("event") id: Long): EspnSummaryDto
}
